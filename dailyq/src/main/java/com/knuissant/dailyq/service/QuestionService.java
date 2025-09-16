package com.knuissant.dailyq.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.constants.QuestionConstants;
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

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserFlowProgressRepository userFlowProgressRepository;

    @Transactional(readOnly = true)
    public RandomQuestionResponse getRandomQuestion(Long userId) {
        UserPreferences prefs = userPreferencesRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        long answeredToday = answerRepository.countTodayByUserId(userId);
        int remain = prefs.getDailyQuestionLimit() - (int) answeredToday;
        if (remain <= 0) {
            throw new BusinessException(ErrorCode.DAILY_LIMIT_REACHED);
        }

        // Use user preferences
        QuestionMode mode = prefs.getQuestionMode();
        Long jobId = prefs.getUserJob() != null ? prefs.getUserJob().getId() : null;

        // Resolve phase when FLOW
        FlowPhase phase = null;
        if (mode == QuestionMode.FLOW) {
            phase = resolvePhase(userId);
        }

        Optional<Question> picked;
        if (mode == QuestionMode.TECH) {
            if (jobId != null) {
                picked = questionRepository.findRandomTechByJobId(jobId, userId);
            } else {
                picked = questionRepository.findRandomByType(QuestionConstants.QUESTION_TYPE_TECH, userId);
            }
        } else { // FLOW
            QuestionType typeForPhase = mapPhaseToType(phase);
            picked = questionRepository.findRandomByType(typeForPhase.name(), userId);
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

    private FlowPhase resolvePhase(Long userId) {
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


