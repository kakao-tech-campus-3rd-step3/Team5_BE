package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.dto.answers.AnswerInfoResponse;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.service.handler.AbstractAnswerHandler;
import com.knuissant.dailyq.service.handler.AnswerHandlerFactory;
import com.knuissant.dailyq.service.handler.FollowUpAnswerHandler;
import com.knuissant.dailyq.service.handler.RegularAnswerHandler;

@Service
@RequiredArgsConstructor
public class AnswerCommandService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final FeedbackService feedbackService;
    private final AnswerHandlerFactory answerHandlerFactory;


    @Transactional
    public AnswerInfoResponse submitAnswer(Long userId, AnswerCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        // 템플릿 메서드 패턴 도입
        AbstractAnswerHandler handler = answerHandlerFactory.createHandler(
                            user, request, isFollowUpQuestion(request.questionId()));

        Answer savedAnswer = handler.handle();

        Feedback savedFeedback = feedbackService.createPendingFeedback(savedAnswer);

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

}
