package com.knuissant.dailyq.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.service.TokenService;

/**
 * 토큰 관련 API를 처리하는 컨트롤러
 * 주로 액세스 토큰 갱신(refresh) 작업을 담당
 */
@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 엔드포인트
    @PostMapping("/api/v1/token/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @CookieValue("refresh_token") String refreshToken) {

        // 토큰 서비스를 통해 새로운 액세스 토큰 생성
        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        // 단일 키-값 쌍을 가진 Map을 생성하여 응답
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }
}
