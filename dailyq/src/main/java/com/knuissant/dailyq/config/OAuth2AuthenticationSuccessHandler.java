package com.knuissant.dailyq.config;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.jwt.TokenProvider;
import com.knuissant.dailyq.repository.UserRepository;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.exception.ErrorCode;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final long refreshTokenExpirationMillis; // 설정 파일에서 주입받을 필드 추가

    // @RequiredArgsConstructor 대신 명시적 생성자로 변경하여 @Value 주입
    public OAuth2AuthenticationSuccessHandler(
            TokenProvider tokenProvider,
            UserRepository userRepository,
            OAuth2AuthenticationFailureHandler failureHandler,
            @Value("${jwt.refresh-token-expiration-millis}") long refreshTokenExpirationMillis) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.failureHandler = failureHandler;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }
    /**
     * OAuth2 인증 성공 시 호출되는 핸들러 메서드
     * 1. OAuth2User로부터 이메일 정보를 추출
     * 2. 해당 이메일로 사용자 조회
     * 3. 액세스 토큰과 리프레시 토큰 생성
     * 4. 리프레시 토큰을 쿠키에 저장
     * 5. 액세스 토큰을 포함한 URL로 리다이렉트
     */

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            // OAuth2User에서 사용자 정보 추출
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = extractEmail(oAuth2User);

            // 이메일이 없는 경우 예외 처리
            if (email == null || email.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, "Email not found in OAuth2 response");
            }

            // 이메일로 사용자 조회
            // 이 시점에는 CustomOAuth2UserService에 의해 유저가 DB에 반드시 존재해야 합니다.
            // 만약 없다면, 이는 서버의 비정상적인 상태이므로 500 에러 계열로 처리합니다.
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "Inconsistent user state after OAuth2 login."));

            // 토큰 생성
            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            // 사용자의 리프레시 토큰 업데이트
            user.updateRefreshToken(refreshToken);

            // 리프레시 토큰을 쿠키에 저장
            addRefreshTokenToCookie(response, refreshToken);

            // 액세스 토큰을 포함한 URL 생성 및 리다이렉트
            String targetUrl = getTargetUrl(accessToken);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception ex) {
            // 이 핸들러 내에서 발생하는 모든 예외는 failureHandler에게 위임하여
            // 클라이언트에게 일관된 JSON 에러 응답을 보내도록 합니다.
            log.error("Authentication success processing failed", ex);
            AuthenticationException authException = new AuthenticationServiceException(ex.getMessage(), ex);
            failureHandler.onAuthenticationFailure(request, response, authException);
        }
    }


    // OAuth2User로부터 이메일 정보를 추출
    private String extractEmail(OAuth2User oAuth2User) {
        // 일반적인 OAuth2 응답에서 이메일 추출 시도
        String email = oAuth2User.getAttribute("email");

        // 카카오 계정의 경우 다른 구조로 이메일 정보가 제공됨
        if (email == null) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
                email = (String) kakaoAccount.get("email");
            }
        }

        return email;
    }

    /**
     * 리프레시 토큰을 HTTP Only 쿠키로 설정
     * 7일간 유효한 보안 쿠키 생성
     */
    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);  // XSS 공격 방지
        refreshTokenCookie.setSecure(true);    // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");       // 모든 경로에서 접근 가능
        // 주입받은 만료 시간(밀리초)을 초 단위로 변환하여 설정
        refreshTokenCookie.setMaxAge((int) (refreshTokenExpirationMillis / 1000));
        response.addCookie(refreshTokenCookie);
    }

    // 액세스 토큰을 포함한 리다이렉트 URL 생성
    private String getTargetUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString("https://dailyq.my")
                .queryParam("token", accessToken)
                .build()
                .toUriString();
    }
}
