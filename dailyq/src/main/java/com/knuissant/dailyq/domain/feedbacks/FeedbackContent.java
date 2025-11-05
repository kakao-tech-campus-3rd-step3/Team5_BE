package com.knuissant.dailyq.domain.feedbacks;

import java.util.List;

public record FeedbackContent(
        List<String> positivePoints,
        List<String> pointsForImprovement
) {

}
