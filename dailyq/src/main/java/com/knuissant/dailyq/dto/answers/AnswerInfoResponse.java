package com.knuissant.dailyq.dto.answers;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;

public record AnswerInfoResponse(
        Long answerId,
        String answerText,  // status == PENDING_STT일 경우, null
        String status,
        Long feedbackId
) {

    public static AnswerInfoResponse from(Answer answer, Feedback feedback) {
        return new AnswerInfoResponse(
                answer.getId(),
                answer.getAnswerText(),
                answer.getStatus().name(),
                feedback.getId()
        );
    }

}
