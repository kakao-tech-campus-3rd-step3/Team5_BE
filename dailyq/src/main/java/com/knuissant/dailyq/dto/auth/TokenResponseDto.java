package com.knuissant.dailyq.dto.auth;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
