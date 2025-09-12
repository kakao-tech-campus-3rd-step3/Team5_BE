package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.AnswerCreateRequest;
import com.knuissant.dailyq.dto.AnswerCreateResponse;
import com.knuissant.dailyq.dto.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.FeedbackRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnswerCreateResponse createAnswerAndFeedback(AnswerCreateRequest request, Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Question question = questionRepository.findById(request.questionId())
            .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        // 추후 audioUrl -> answerText로 반환 후 저장 로직 추가
        Answer answer = Answer.create(user, question, request.answerText());
        Answer savedAnswer = answerRepository.save(answer);

        Feedback feedback = Feedback.create(savedAnswer, FeedbackStatus.PENDING);
        Feedback savedFeedback = feedbackRepository.save(feedback);

        return new AnswerCreateResponse(savedAnswer.getId(), savedFeedback.getId());
    }

    @Transactional
    public AnswerLevelUpdateResponse updateAnswerLevel(Long answerId,
            AnswerLevelUpdateRequest request) {

        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        answer.updateLevel(request.level());

        return new AnswerLevelUpdateResponse(answer.getId(), answer.getLevel());
    }
}
