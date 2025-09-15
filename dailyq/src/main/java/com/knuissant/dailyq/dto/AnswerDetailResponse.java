package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;
import java.time.LocalDateTime;

public record AnswerDetailResponse(
        Long answerId,
        QuestionSummary question,
        String answerText,
        Integer level,
        Boolean starred,
        LocalDateTime answeredTime,
        FeedbackDetail feedback
) {

    public record QuestionSummary(Long questionId, QuestionType questionType, String questionText
    ) {

        public static QuestionSummary from(Question question) {
            return new QuestionSummary(
                    question.getId(),
                    question.getQuestionType(),
                    question.getQuestionText()
            );
        }
    }

    public record FeedbackDetail(FeedbackStatus status, String feedbackText, LocalDateTime updatedAt
    ) {

        public static FeedbackDetail from(Feedback feedback) {
            return new FeedbackDetail(
                    feedback.getStatus(),
                    feedback.getContent(),
                    feedback.getUpdatedAt()
            );
        }
    }

    public static AnswerDetailResponse of(Answer answer, Feedback feedback) {
        return new AnswerDetailResponse(
                answer.getId(),
                QuestionSummary.from(answer.getQuestion()),
                answer.getAnswerText(),
                answer.getLevel(),
                answer.getStarred(),
                answer.getAnsweredTime(),
                (feedback != null) ? FeedbackDetail.from(feedback) : null
        );
    }
}
