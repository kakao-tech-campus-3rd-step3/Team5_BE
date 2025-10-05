package com.knuissant.dailyq.dto.feedbacks;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;

public record FeedbackResponse(
        String overallEvaluation,
        List<String> positivePoints,
        List<String> pointsForImprovement
) {

    public static FeedbackResponse from(String content, ObjectMapper objectMapper) {

        try {
            return objectMapper.readValue(content, FeedbackResponse.class);
        } catch (JsonProcessingException e) {
            throw new InfraException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }
}
