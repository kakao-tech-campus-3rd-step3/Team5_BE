package com.knuissant.dailyq.dto.feedbacks;

import java.time.LocalDateTime;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackContent;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;

public record FeedbackResponse(
        FeedbackStatus status,
        FeedbackContent content,
        LocalDateTime updatedAt,
        boolean followUp
) {

    public static FeedbackResponse of(Feedback feedback, boolean followUp) {
        return new FeedbackResponse(
                feedback.getStatus(),
                feedback.getContent(),
                feedback.getUpdatedAt(),
                followUp
        );
    }
}
