package com.knuissant.dailyq.dto.answers;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;

public record AnswerCreateResponse(
        Long answerId,
        String answerText,  // status == PENDING_STT일 경우, null
        String status,
        Long feedbackId
) {

    public static AnswerCreateResponse from(Answer answer, Feedback feedback) {
        return new AnswerCreateResponse(
                answer.getId(),
                answer.getAnswerText(),
                answer.getStatus().name(),
                feedback.getId()
        );
    }

}
