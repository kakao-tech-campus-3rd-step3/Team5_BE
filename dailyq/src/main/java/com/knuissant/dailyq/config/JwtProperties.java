package com.knuissant.dailyq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml의 'jwt' 하위 프로퍼티를 바인딩하는 설정 클래스입니다.
 * @param secret JWT 서명에 사용될 비밀 키
 * @param accessTokenExpirationMillis 액세스 토큰 만료 시간 (밀리초)
 * @param refreshTokenExpirationMillis 리프레시 토큰 만료 시간 (밀리초)
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMillis,
        long refreshTokenExpirationMillis
) {}
