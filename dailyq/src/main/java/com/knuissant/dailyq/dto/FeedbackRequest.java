package com.knuissant.dailyq.dto;

public record FeedbackRequest(
        Long questionId,
        String userAnswer
) {

}
