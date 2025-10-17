package com.knuissant.dailyq.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.UserFlowProgress;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
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
    private final FollowUpQuestionService followUpQuestionService;

    @Transactional(readOnly = true)
    public RandomQuestionResponse getRandomQuestion(Long userId) {
        UserPreferences prefs = userPreferencesRepository.findById(userId)
                .orElseThrow(() -> new InfraException(ErrorCode.USER_PREFERENCES_NOT_FOUND, userId));

        validateDailyQuestionLimit(userId, prefs);

        // Use user preferences
        QuestionMode mode = prefs.getQuestionMode();
        Long jobId = Optional.ofNullable(prefs.getUserJob())
                .map(job -> job.getId())
                .orElseThrow(() -> new InfraException(ErrorCode.USER_JOB_NOT_SET));

        // Resolve phase when FLOW
        final FlowPhase phase = resolvePhase(userId, mode);

        return selectRandomQuestion(mode, phase, jobId, userId, prefs.getTimeLimitSeconds())
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_QUESTION_AVAILABLE, mode.name(), phase.name(), jobId));
    }

    private FlowPhase resolvePhase(Long userId, QuestionMode mode) {
        if (mode != QuestionMode.FLOW) {
            return null;
        }

        UserFlowProgress progress = userFlowProgressRepository.findById(userId)
                .orElseThrow(() -> new InfraException(ErrorCode.USER_FLOW_PROGRESS_NOT_FOUND, userId));
        return progress.getNextPhase();
    }

    private Optional<RandomQuestionResponse> selectRandomQuestion(QuestionMode mode, FlowPhase phase, Long jobId, Long userId, int timeLimitSeconds) {
        // 1. 먼저 미답변 꼬리질문 확인
        Optional<RandomQuestionResponse> followUpQuestion = findUnansweredFollowUpQuestion(userId, jobId, timeLimitSeconds);
        if (followUpQuestion.isPresent()) {
            return followUpQuestion;
        }

        // 2. 일반 질문 제공
        return switch (mode) {
            case TECH -> findRandomTechQuestion(jobId, userId, mode, phase, timeLimitSeconds);
            case FLOW -> findRandomFlowQuestion(phase, userId, jobId, mode, timeLimitSeconds);
        };
    }

    private void validateDailyQuestionLimit(Long userId, UserPreferences userPreferences) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        long answeredToday = answerRepository.countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);
        int remain = userPreferences.getDailyQuestionLimit() - (int) answeredToday;

        Optional.of(remain)
                .filter(r -> r > 0)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_LIMIT_REACHED, remain));
    }

    private Optional<RandomQuestionResponse> findRandomTechQuestion(Long jobId, Long userId, QuestionMode mode, FlowPhase phase, int timeLimitSeconds) {
        // MAX ID 조회
        Long maxId = questionRepository.findMaxAvailableTechQuestionId(jobId, userId);
        if (maxId == null) {
            return Optional.empty();
        }

        // 랜덤 ID 생성 (1부터 maxId까지)
        long randomId = ThreadLocalRandom.current().nextLong(1, maxId + 1);

        // cursor 기반 조회 (id >= randomId인 첫 번째 질문)
        Pageable pageable = PageRequest.of(0, 1);
        List<Question> questions = questionRepository.findAvailableTechQuestionsFromCursor(jobId, randomId, userId, pageable);

        if (questions.isEmpty()) {
            return Optional.empty();
        }

        Question q = questions.get(0);
        return Optional.of(RandomQuestionResponse.from(q, mode, phase, jobId, timeLimitSeconds));
    }

    private Optional<RandomQuestionResponse> findRandomFlowQuestion(FlowPhase phase, Long userId, Long jobId, QuestionMode mode, int timeLimitSeconds) {
        QuestionType questionType = mapPhaseToType(phase);

        // MAX ID 조회
        Long maxId = questionRepository.findMaxAvailableQuestionId(questionType, userId);
        if (maxId == null) {
            return Optional.empty();
        }

        // 랜덤 ID 생성 (1부터 maxId까지)
        long randomId = ThreadLocalRandom.current().nextLong(1, maxId + 1);

        // cursor 기반 조회 (id >= randomId인 첫 번째 질문)
        Pageable pageable = PageRequest.of(0, 1);
        List<Question> questions = questionRepository.findAvailableQuestionsFromCursor(questionType, randomId, userId, pageable);

        if (questions.isEmpty()) {
            return Optional.empty();
        }

        Question q = questions.get(0);
        return Optional.of(RandomQuestionResponse.from(q, mode, phase, jobId, timeLimitSeconds));
    }

    private QuestionType mapPhaseToType(FlowPhase phase) {
        return switch (phase) {
            case INTRO -> QuestionType.INTRO;
            case MOTIVATION -> QuestionType.MOTIVATION;
            case TECH1, TECH2 -> QuestionType.TECH;
            case PERSONALITY -> QuestionType.PERSONALITY;
        };
    }

    /**
     * 사용자의 미답변 꼬리질문 조회 및 DTO 변환
     */
    private Optional<RandomQuestionResponse> findUnansweredFollowUpQuestion(Long userId, Long jobId, int timeLimitSeconds) {
        List<FollowUpQuestion> unansweredFollowUps = followUpQuestionService.getUnansweredFollowUpQuestions(userId);

        if (unansweredFollowUps.isEmpty()) {
            return Optional.empty();
        }

        FollowUpQuestion fq = unansweredFollowUps.get(0);
        return Optional.of(RandomQuestionResponse.fromFollowUp(fq, jobId, timeLimitSeconds));
    }
}


