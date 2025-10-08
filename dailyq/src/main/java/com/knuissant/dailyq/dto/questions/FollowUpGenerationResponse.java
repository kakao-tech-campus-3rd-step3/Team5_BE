package com.knuissant.dailyq.dto.questions;

public record FollowUpGenerationResponse(
    String message,
    int generatedCount
) {
    public static FollowUpGenerationResponse of(int count) {
        return new FollowUpGenerationResponse(
            "꼬리질문이 생성되었습니다",
            count
        );
    }
}

