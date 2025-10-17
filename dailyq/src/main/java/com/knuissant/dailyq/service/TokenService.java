package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.jwt.TokenProvider;
import com.knuissant.dailyq.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    /**
     * 클라이언트로부터 받은 Refresh Token을 사용하여 새로운 Access Token을 생성하는 메소드입니다.
     *
     * @param refreshToken 클라이언트의 쿠키에 담겨있던 Refresh Token 문자열
     * @return 검증 성공 시, 새로 발급된 Access Token 문자열
     * @throws BusinessException 검증 과정에서 하나라도 실패하면 예외 발생
     */
    @Transactional(readOnly = true)
    public String createNewAccessToken(String refreshToken) {
        // --- 1단계 검증: 토큰의 유효성 확인 ---
        // 토큰이 만료되었거나, 서명이 유효하지 않은 경우 BusinessException을 발생시킵니다.
        // JwtAuthenticationFilter와 일관성을 위해, validateToken 내부에서 발생하는 예외를 그대로 두거나
        // 여기서 명시적으로 INVALID_TOKEN을 사용할 수 있습니다.
        if (!tokenProvider.validateToken(refreshToken)) {
            // validateToken에서 이미 JwtException 계열 예외를 던진다면 이 라인은 필요 없을 수 있습니다.
            // 명시적으로 처리하고 싶을 경우 이 코드를 사용합니다.
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Unexpected refresh token");
        }

        // --- 2단계 검증: 토큰에서 사용자 ID 추출 및 DB 조회 ---
        // 토큰은 유효하지만, 해당 ID의 사용자가 DB에 없는 경우(탈퇴 등) 예외를 발생시킵니다.
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        // --- 3단계 검증 (가장 중요!): DB에 저장된 토큰과 일치 여부 확인 ---
        // DB에 저장된 토큰과 클라이언트가 보낸 토큰이 일치하지 않는다면,
        // 탈취되었거나 더 이상 유효하지 않은 토큰일 가능성이 높으므로 예외를 발생시킵니다.
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh token mismatch");
        }

        // --- 모든 검증 통과: 새로운 Access Token 생성 ---
        return tokenProvider.generateAccessToken(user);
    }
}

