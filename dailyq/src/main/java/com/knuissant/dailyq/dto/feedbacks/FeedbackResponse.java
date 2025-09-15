package com.knuissant.dailyq.dto.feedbacks;

import java.util.List;

public record FeedbackResponse(
        String overallEvaluation,
        List<String> positivePoints,
        List<String> pointsForImprovement
) {

}
