package com.knuissant.dailyq.dto.answers;

import java.time.LocalDateTime;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;

public record AnswerDetailResponse(
        Long answerId,
        QuestionSummary question,
        String answerText,
        String memo,
        Integer level,
        Boolean starred,
        LocalDateTime createdAt,
        FeedbackResponse feedback
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

    public static AnswerDetailResponse of(Answer answer, Feedback feedback) {
        return new AnswerDetailResponse(
                answer.getId(),
                QuestionSummary.from(answer.getQuestion()),
                answer.getAnswerText(),
                answer.getMemo(),
                answer.getLevel(),
                answer.getStarred(),
                answer.getCreatedAt(),
                (feedback != null) ? FeedbackResponse.from(feedback) : null
        );
    }
}
