package com.knuissant.dailyq.feedback.dto;

public record FeedbackRequest(
        Long questionId,
        String userAnswer
) {

}
