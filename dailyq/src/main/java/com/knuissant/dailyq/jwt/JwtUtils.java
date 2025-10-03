package com.knuissant.dailyq.jwt;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final TokenProvider tokenProvider;

    /**
     * HTTP 요청에서 JWT 토큰을 추출하여 사용자 ID를 반환합니다.
     * 
     * @param request HTTP 요청 객체
     * @return 사용자 ID
     * @throws BusinessException Authorization 헤더가 없거나 잘못된 형식인 경우, 토큰이 유효하지 않은 경우
     */
    public Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "Authorization 헤더가 없거나 잘못된 형식입니다.");
        }
        
        String token = authHeader.substring(7); // "Bearer " 제거
        
        if (!tokenProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
        }
        
        return tokenProvider.getUserIdFromToken(token);
    }
}