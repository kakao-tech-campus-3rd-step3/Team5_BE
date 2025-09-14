package com.knuissant.dailyq.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

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

        // 일일 질문 제한 확인
        validateDailyQuestionLimit(userId, prefs);

        // 사용자 설정 정보
        QuestionMode mode = prefs.getQuestionMode();
        Long jobId = prefs.getUserJob() != null ? prefs.getUserJob().getId() : null;
        FlowPhase phase = null;
        if (mode == QuestionMode.FLOW) {
            phase = resolvePhase(userId);
        }

        Question q = getRandomQuestionOptimized(mode, phase, jobId, userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NO_QUESTION_AVAILABLE));

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

    private Optional<Question> getRandomQuestionOptimized(QuestionMode mode, FlowPhase phase, Long jobId, Long userId) {
        // 사용자가 답변한 질문들을 조회
        Set<Long> answeredQuestionIds = answerRepository.findByUserId(userId)
            .stream()
            .map(answer -> answer.getQuestion().getId())
            .collect(Collectors.toSet());

        // 질문 모드에 따라 질문 조회
        List<Question> availableQuestions = findAvailableQuestions(mode, jobId, phase);

        // 답변하지 않은 질문들만 필터링
        List<Question> unansweredQuestions = availableQuestions.stream()
            .filter(question -> !answeredQuestionIds.contains(question.getId()))
            .collect(Collectors.toList());

        if (unansweredQuestions.isEmpty()) {
            return Optional.empty();
        }

        // 애플리케이션 레벨에서 랜덤 선택
        return Optional.of(unansweredQuestions.get(ThreadLocalRandom.current().nextInt(unansweredQuestions.size())));
    }

    private void validateDailyQuestionLimit(Long userId, UserPreferences userPreferences) {
        long answeredToday = answerRepository.countTodayByUserId(userId);
        int remain = userPreferences.getDailyQuestionLimit() - (int) answeredToday;
        
        if (remain <= 0) {
            throw new BusinessException(ErrorCode.DAILY_LIMIT_REACHED);
        }
    }

    // 질문 모드에 따라 사용 가능한 질문 목록을 조회
    private List<Question> findAvailableQuestions(QuestionMode mode, Long jobId, FlowPhase phase) {
        return switch (mode) {
            case TECH -> findTechQuestions(jobId);
            case FLOW -> findFlowQuestions(phase);
        };
    }

    // TECH 모드에 맞는 질문들을 조회
    private List<Question> findTechQuestions(Long jobId) {
        if (jobId != null) {
            return questionRepository.findByEnabledTrueAndQuestionTypeAndJobsId(QuestionType.TECH, jobId);
        } else {
            return questionRepository.findByEnabledTrueAndQuestionType(QuestionType.TECH);
        }
    }

    // FLOW 모드에 맞는 질문들을 조회
    private List<Question> findFlowQuestions(FlowPhase phase) {
        QuestionType typeForPhase = mapPhaseToType(phase);
        return questionRepository.findByEnabledTrueAndQuestionType(typeForPhase);
    }

    // 플로우 단계를 질문 타입으로 매핑
    private QuestionType mapPhaseToType(FlowPhase phase) {
        return switch (phase) {
            case INTRO -> QuestionType.INTRO;
            case MOTIVATION -> QuestionType.MOTIVATION;
            case TECH1, TECH2 -> QuestionType.TECH;
            case PERSONALITY -> QuestionType.PERSONALITY;
        };
    }
}


