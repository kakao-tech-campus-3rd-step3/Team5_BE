package com.knuissant.dailyq.feedback.dto;

import java.util.List;

public record FeedbackResponse(
        String overallEvaluation,
        List<String> positivePoints,
        List<String> pointsForImprovement
) {

}
