package com.knuissant.dailyq.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuissant.dailyq.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 소셜 로그인 실패 시 호출되는 커스텀 핸들러입니다.
 * 소셜 로그인 과정에서 발생하는 예외를 일관된 ProblemDetail JSON 형식으로 응답합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.warn("OAuth2 Login Failed: {}", exception.getMessage(), exception);

        // 소셜 로그인 실패 시 INVALID_SOCIAL_LOGIN 에러 코드를 사용합니다.
        ErrorCode errorCode = ErrorCode.INVALID_SOCIAL_LOGIN;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ProblemDetail problemDetail = errorCode.toProblemDetail();
        // 에러의 상세 원인을 ProblemDetail에 추가해 클라이언트가 더 자세한 정보를 알 수 있게 합니다.
        problemDetail.setProperty("error_message", exception.getLocalizedMessage());

        String jsonResponse = objectMapper.writeValueAsString(problemDetail);
        response.getWriter().write(jsonResponse);
    }
}
