package com.knuissant.dailyq.dto.feedbacks;

import java.util.List;

import com.knuissant.dailyq.domain.feedbacks.FeedbackContent;

public record FeedbackResponse(
        List<String> positivePoints,
        List<String> pointsForImprovement
) {

    public static FeedbackResponse from(FeedbackContent content) {

        return new FeedbackResponse(
                content.positivePoints(),
                content.pointsForImprovement()
        );
    }
}
