package com.knuissant.dailyq.service;

import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.UserFlowProgress;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserFlowProgressRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserFlowProgressRepository userFlowProgressRepository;

    @Transactional(readOnly = true)
    public RandomQuestionResponse getRandomQuestion(Long userId, String overrideMode, String overridePhase, Long overrideJobId) {
        UserPreferences prefs = userPreferencesRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        long answeredToday = answerRepository.countTodayByUserId(userId);
        int remain = prefs.getDailyQuestionLimit() - (int) answeredToday;
        if (remain <= 0) {
            throw new BusinessException(ErrorCode.DAILY_LIMIT_REACHED);
        }

        // Resolve mode
        QuestionMode mode = resolveMode(overrideMode, prefs.getQuestionMode());

        // Resolve job override when TECH
        Long jobId = overrideJobId != null ? overrideJobId : (prefs.getUserJob() != null ? prefs.getUserJob().getId() : null);

        // Resolve phase when FLOW
        FlowPhase phase = null;
        if (mode == QuestionMode.FLOW) {
            phase = resolvePhase(userId, overridePhase);
        }

        Optional<Question> picked;
        if (mode == QuestionMode.TECH) {
            if (jobId != null) {
                picked = questionRepository.findRandomTechByJobIdExcludingTodayAnswers(jobId, userId);
            } else {
                picked = questionRepository.findRandomByTypeExcludingTodayAnswers(QuestionType.TECH.name(), userId);
            }
        } else { // FLOW
            QuestionType typeForPhase = mapPhaseToType(phase);
            picked = questionRepository.findRandomByTypeExcludingTodayAnswers(typeForPhase.name(), userId);
        }

        Question q = picked.orElseThrow(() -> new BusinessException(ErrorCode.NO_QUESTION_AVAILABLE));

        return new RandomQuestionResponse(
            q.getId(),
            q.getQuestionType(),
            mode == QuestionMode.FLOW ? phase : null,
            q.getQuestionText(),
            jobId,
            prefs.getTimeLimitSeconds()
        );
    }

    private QuestionMode resolveMode(String overrideMode, QuestionMode defaultMode) {
        if (overrideMode == null) return defaultMode;
        return QuestionMode.valueOf(overrideMode.toUpperCase());
    }

    private FlowPhase resolvePhase(Long userId, String overridePhase) {
        if (overridePhase != null) {
            return FlowPhase.valueOf(overridePhase.toUpperCase());
        }
        UserFlowProgress progress = userFlowProgressRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return progress.getNextPhase();
    }

    private QuestionType mapPhaseToType(FlowPhase phase) {
        return switch (phase) {
            case INTRO -> QuestionType.INTRO;
            case MOTIVATION -> QuestionType.MOTIVATION;
            case TECH1, TECH2 -> QuestionType.TECH;
            case PERSONALITY -> QuestionType.PERSONALITY;
        };
    }
}


