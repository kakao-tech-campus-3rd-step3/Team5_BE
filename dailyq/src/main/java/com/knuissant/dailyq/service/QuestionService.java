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
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.UserFlowProgress;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserFlowProgressRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;

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
            .orElseThrow(() -> new InfraException(ErrorCode.USER_PREFERENCES_NOT_FOUND));

        validateDailyQuestionLimit(userId, prefs);

        // Use user preferences
        QuestionMode mode = prefs.getQuestionMode();
        Long jobId = Optional.ofNullable(prefs.getUserJob())
            .map(job -> job.getId())
            .orElseThrow(() -> new InfraException(ErrorCode.USER_JOB_NOT_SET));

        // Resolve phase when FLOW
        final FlowPhase phase = resolvePhase(userId, mode);

        Question q = selectRandomQuestion(mode, phase, jobId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NO_QUESTION_AVAILABLE, mode.name(), phase.name(), jobId));

        return new RandomQuestionResponse(
            q.getId(),
            q.getQuestionType(),
            mode == QuestionMode.FLOW ? phase : null,
            q.getQuestionText(),
            jobId,
            prefs.getTimeLimitSeconds()
        );
    }

    private FlowPhase resolvePhase(Long userId, QuestionMode mode) {
        if (mode != QuestionMode.FLOW) {
            return null;
        }
        
        UserFlowProgress progress = userFlowProgressRepository.findById(userId)
            .orElseThrow(() -> new InfraException(ErrorCode.USER_FLOW_PROGRESS_NOT_FOUND));
        return progress.getNextPhase();
    }

    private Optional<Question> selectRandomQuestion(QuestionMode mode, FlowPhase phase, Long jobId, Long userId) {
        // 1. 먼저 미답변 꼬리질문 확인
        Optional<Question> followUpQuestion = findUnansweredFollowUpQuestion(userId);
        if (followUpQuestion.isPresent()) {
            return followUpQuestion;
        }

        // 2. 일반 질문 제공
        return switch (mode) {
            case TECH -> findRandomTechQuestion(jobId, userId);
            case FLOW -> findRandomFlowQuestion(phase, userId);
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

    private Optional<Question> findRandomTechQuestion(Long jobId, Long userId) {
        // count 조회 후 랜덤 offset 생성
        long count = questionRepository.countAvailableTechQuestions(jobId, userId);
        if (count == 0) {
            return Optional.empty();
        }
        
        // count가 int 범위를 넘으면 int 최대값(약 21억)으로 제한
        int safeCount = count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count;
        int randomOffset = ThreadLocalRandom.current().nextInt(safeCount);
        Pageable pageable = PageRequest.of(randomOffset, 1);
        List<Question> questions = questionRepository.findAvailableTechQuestionsByJobId(jobId, userId, pageable);
        return questions.isEmpty() ? Optional.empty() : Optional.of(questions.get(0));
    }

    private Optional<Question> findRandomFlowQuestion(FlowPhase phase, Long userId) {
        QuestionType questionType = mapPhaseToType(phase);
        
        long count = questionRepository.countAvailableQuestions(questionType, userId);
        if (count == 0) {
            return Optional.empty();
        }
        
        // count가 int 범위를 넘으면 int 최대값(약 21억)으로 제한
        int safeCount = count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count;
        int randomOffset = ThreadLocalRandom.current().nextInt(safeCount);
        Pageable pageable = PageRequest.of(randomOffset, 1);
        List<Question> questions = questionRepository.findAvailableQuestionsByType(questionType, userId, pageable);
        return questions.isEmpty() ? Optional.empty() : Optional.of(questions.get(0));
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
     * 사용자의 미답변 꼬리질문 조회
     */
    private Optional<Question> findUnansweredFollowUpQuestion(Long userId) {
        List<FollowUpQuestion> unansweredFollowUps = followUpQuestionService.getUnansweredFollowUpQuestions(userId);
        return unansweredFollowUps.isEmpty() ? Optional.empty() : Optional.of(convertToQuestion(unansweredFollowUps.get(0)));
    }

    /**
     * 꼬리질문을 일반 Question으로 변환
     */
    private Question convertToQuestion(FollowUpQuestion followUpQuestion) {
        return Question.builder()
            .id(-followUpQuestion.getId()) // 음수 ID로 꼬리질문 구분
            .questionText(followUpQuestion.getQuestionText())
            .questionType(QuestionType.TECH) // 꼬리질문은 일반적으로 TECH 타입으로 분류
            .enabled(true)
            .createdAt(followUpQuestion.getCreatedAt())
            .updatedAt(followUpQuestion.getCreatedAt())
            .build();
    }
}


