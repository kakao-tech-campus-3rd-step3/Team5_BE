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
import com.knuissant.dailyq.domain.users.User;
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
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserFlowProgressRepository userFlowProgressRepository;
    private final UserRepository userRepository;
    private final FollowUpQuestionService followUpQuestionService;

    @Transactional
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
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_QUESTION_AVAILABLE,
                        "mode:", mode.name(), "phase:", phase.name(), "jobId:", jobId));
    }

    /**
     * 사용자의 현재 phase를 조회합니다.
     * <p>
     * TECH 모드일 때는 FLOW progress를 무시하고 null을 반환합니다. FLOW 모드일 때만 DB에서 조회하여 phase를 반환합니다.
     */
    private FlowPhase resolvePhase(Long userId, QuestionMode mode) {
        // TECH 모드일 때는 FLOW progress를 사용하지 않음
        if (mode != QuestionMode.FLOW) {
            return null;
        }

        // FLOW 모드일 때만 DB에서 조회 (없으면 생성)
        return userFlowProgressRepository.findById(userId)
                .map(UserFlowProgress::getNextPhase)
                .orElseGet(() -> createDefaultUserFlowProgress(userId).getNextPhase());
    }

    /**
     * 사용자의 기본 UserFlowProgress를 생성합니다.
     */
    private UserFlowProgress createDefaultUserFlowProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InfraException(ErrorCode.USER_NOT_FOUND, userId));

        return userFlowProgressRepository.save(UserFlowProgress.create(user));
    }

    private Optional<RandomQuestionResponse> selectRandomQuestion(QuestionMode mode, FlowPhase phase, Long jobId, Long userId, int timeLimitSeconds) {
        // 1. 먼저 미답변 꼬리질문 확인
        Optional<RandomQuestionResponse> followUpQuestion = followUpQuestionService.getUnansweredFollowUpQuestion(userId)
                .map(fq -> RandomQuestionResponse.fromFollowUp(fq, jobId, timeLimitSeconds));
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
        // 꼬리질문은 일일 제한에서 제외
        long answeredToday = answerRepository.countByUserIdAndFollowUpQuestionIsNullAndCreatedAtBetween(userId, startOfDay, endOfDay);
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
}


