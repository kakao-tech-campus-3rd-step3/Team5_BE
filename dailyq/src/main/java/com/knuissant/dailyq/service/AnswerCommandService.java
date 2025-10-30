package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.dto.answers.AnswerCreateResponse;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.AnswerRepository;
import com.knuissant.dailyq.repository.QuestionRepository;
import com.knuissant.dailyq.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AnswerCommandService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final FeedbackService feedbackService;
    private final FollowUpQuestionService followUpQuestionService;
    private final QuestionRepository questionRepository;


    @Transactional
    public AnswerCreateResponse submitAnswer(Long userId, AnswerCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Answer savedAnswer = request.followUp()
                ? handleFollowUpQuestionAnswer(request, user)
                : handleRegularQuestionAnswer(request, user);

        Feedback savedFeedback = feedbackService.createPendingFeedback(savedAnswer);

        return AnswerCreateResponse.from(savedAnswer, savedFeedback);
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

    private Answer handleFollowUpQuestionAnswer(AnswerCreateRequest request, User user) {
        Long followUpQuestionId = request.questionId();
        FollowUpQuestion followUpQuestion = followUpQuestionService.getFollowUpQuestion(followUpQuestionId);

        if (!followUpQuestion.getAnswer().getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, "userId:", user.getId(), "followUpQuestionId:", followUpQuestionId);
        }
        Question question = followUpQuestion.getAnswer().getQuestion();

        // 추후 audioUrl -> answerText로 반환 후 저장 로직 추가
        Answer answer = Answer.create(user, question, request.answerText());
        answer.setFollowUpQuestion(followUpQuestion);

        Answer savedAnswer = answerRepository.save(answer);
        followUpQuestionService.markFollowUpQuestionAsAnswered(followUpQuestionId);

        return savedAnswer;
    }

    private Answer handleRegularQuestionAnswer(AnswerCreateRequest request, User user) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, request.questionId()));

        // 추후 audioUrl -> answerText로 반환 후 저장 로직 추가
        Answer answer = Answer.create(user, question, request.answerText());
        return answerRepository.save(answer);
    }
}
