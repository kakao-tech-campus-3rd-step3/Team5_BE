package com.knuissant.dailyq.dto.rivals;

public record RivalSearchResponse(
        Long userId,
        String email,
        String name
) {

    public static RivalSearchResponse from(Long userId, String name, String email) {
        return new RivalSearchResponse(
                userId, name, email
        );
    }
}
