package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.domain.users.UserFlowProgress;
import com.knuissant.dailyq.domain.users.UserPreferences;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.dto.answers.AnswerInfoResponse;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.UserFlowProgressRepository;
import com.knuissant.dailyq.repository.UserPreferencesRepository;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.service.handler.AbstractAnswerHandler;
import com.knuissant.dailyq.service.handler.AnswerHandlerFactory;

@Service
@RequiredArgsConstructor
public class AnswerCommandService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final FeedbackService feedbackService;
    private final AnswerHandlerFactory answerHandlerFactory;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserFlowProgressRepository userFlowProgressRepository;


    @Transactional
    public AnswerInfoResponse submitAnswer(Long userId, AnswerCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        // 템플릿 메서드 패턴 도입
        AbstractAnswerHandler handler = answerHandlerFactory.createHandler(
                            user, request, isFollowUpQuestion(request.questionId()));

        Answer savedAnswer = handler.handle();

        Feedback savedFeedback = feedbackService.createPendingFeedback(savedAnswer);

        // FLOW 질문 답변 완료 시 nextPhase 업데이트 (꼬리질문 제외)
        updateFlowProgressIfNeeded(userId, savedAnswer);

        return AnswerInfoResponse.from(savedAnswer, savedFeedback);
    }

    @Transactional
    public AnswerLevelUpdateResponse updateAnswerLevel(Long userId, Long answerId,
            AnswerLevelUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, answerId));
        answer.checkOwnership(userId);

        answer.updateLevel(request.level());
        return AnswerLevelUpdateResponse.from(answer);
    }

    @Transactional
    public AnswerArchiveUpdateResponse updateAnswer(Long userId, Long answerId,
            AnswerArchiveUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND, answerId));
        answer.checkOwnership(userId);

        if (request.memo() != null) {
            answer.updateMemo(request.memo());
        }

        if (request.starred() != null) {
            answer.updateStarred(request.starred());
        }

        if (request.level() != null) {
            answer.updateLevel(request.level());
        }

        return AnswerArchiveUpdateResponse.from(answer);
    }

    private boolean isFollowUpQuestion(Long questionId) {
        return questionId < 0;
    }

    /**
     * FLOW 모드에서 일반 질문(꼬리질문 제외)에 답변한 경우 nextPhase를 업데이트합니다.
     * 
     * TECH 모드일 때는 FLOW progress를 무시합니다.
     */
    private void updateFlowProgressIfNeeded(Long userId, Answer answer) {
        // 꼬리질문은 제외
        if (answer.getFollowUpQuestion() != null) {
            return;
        }

        // 사용자의 현재 모드 확인 - DB에서 조회하여 확인
        UserPreferences preferences = userPreferencesRepository.findById(userId)
                .orElse(null);

        // TECH 모드이거나 preferences가 없으면 무시 (FLOW progress를 사용하지 않음)
        if (preferences == null || preferences.getQuestionMode() != QuestionMode.FLOW) {
            return;
        }

        // FLOW 모드일 때만 progress 업데이트
        // QuestionType이 INTRO, MOTIVATION, PERSONALITY, TECH 중 하나이면 FLOW 질문
        Question question = answer.getQuestion();
        QuestionType questionType = question.getQuestionType();

        if (questionType == QuestionType.INTRO || 
            questionType == QuestionType.MOTIVATION || 
            questionType == QuestionType.PERSONALITY ||
            questionType == QuestionType.TECH) {
            
            // FLOW 모드에서 받은 질문이므로 UserFlowProgress 업데이트
            userFlowProgressRepository.findById(userId)
                    .ifPresent(UserFlowProgress::moveToNextPhase);
        }
    }

}
