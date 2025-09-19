package com.knuissant.dailyq.dto.answers;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;

public record AnswerCreateResponse(
        Long answerId,
        String answerText,
        Long feedbackId
) {

    public static AnswerCreateResponse from(Answer answer, Feedback feedback) {
        return new AnswerCreateResponse(
                answer.getId(),
                answer.getAnswerText(),
                feedback.getId()
        );
    }

}
