package com.knuissant.dailyq.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.service.TokenService;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

/**
 * 토큰 관련 API를 처리하는 컨트롤러
 * 주로 액세스 토큰 갱신(refresh) 작업을 담당
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenApiController {

    private final TokenService tokenService;

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 엔드포인트입니다.
     * @param refreshToken 쿠키에서 가져온 리프레시 토큰
     * @return 성공 시 새로운 액세스 토큰을 포함한 응답
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        // 1. 쿠키에 리프레시 토큰이 없는 경우, 예외를 발생시킵니다.
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 2. 토큰 서비스를 통해 새로운 액세스 토큰 생성을 시도합니다.
        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        // 3. 성공 시, 새로운 액세스 토큰을 응답합니다.
        return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
    }
}