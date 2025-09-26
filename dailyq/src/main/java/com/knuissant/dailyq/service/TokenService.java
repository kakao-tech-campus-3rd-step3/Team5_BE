package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.jwt.TokenProvider;
import com.knuissant.dailyq.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    /**
     * 클라이언트로부터 받은 Refresh Token을 사용하여 새로운 Access Token을 생성하는 메소드입니다.
     * @param refreshToken 클라이언트의 쿠키에 담겨있던 Refresh Token 문자열
     * @return 검증 성공 시, 새로 발급된 Access Token 문자열
     * @throws IllegalArgumentException 검증 과정에서 하나라도 실패하면 예외 발생
     */
    @Transactional(readOnly = true) // 이 메소드는 DB의 데이터를 변경하지 않으므로, 성능 향상을 위해 readOnly로 설정합니다.
    public String createNewAccessToken(String refreshToken) {
        // --- 1단계 검증: 토큰의 유효성 확인 ---
        // 전달받은 Refresh Token이 위변조되지 않았는지, 만료되지는 않았는지 기본적인 검증을 수행합니다.
        // 이 검증에 실패하면, 유효하지 않은 토큰이므로 즉시 예외를 발생시킵니다.
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected refresh token");
        }

        // --- 2단계 검증: 토큰에서 사용자 ID 추출 및 DB 조회 ---
        // 토큰이 유효하다면, 토큰 내부에 저장된 사용자 ID를 추출합니다.
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        // 추출된 ID를 사용하여 DB에서 실제 사용자가 있는지 조회합니다.
        // 만약 해당 ID의 사용자가 없다면(탈퇴 등), 유령 토큰이므로 예외를 발생시킵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // --- 3단계 검증 (가장 중요!): DB에 저장된 토큰과 일치 여부 확인 ---
        // 조회된 사용자의 DB에 저장된 Refresh Token과, 클라이언트가 전달한 Refresh Token이 정확히 일치하는지 확인합니다.
        // 이 검증을 통해, 이전에 발급되었지만 탈취당했을 수 있는 '오래된' Refresh Token의 사용을 막을 수 있습니다.
        // 사용자가 새로 로그인할 때마다 Refresh Token은 갱신되므로, DB에는 항상 가장 최신의 토큰만 저장되어야 합니다.
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        // --- 모든 검증 통과: 새로운 Access Token 생성 ---
        // 위 3단계의 보안 검증을 모두 통과했다면, 이 Refresh Token은 안전하다고 판단할 수 있습니다.
        // 해당 사용자 정보를 기반으로 새로운 Access Token을 생성하여 반환합니다.
        return tokenProvider.generateAccessToken(user);
    }
}

