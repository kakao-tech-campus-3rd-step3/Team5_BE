package com.knuissant.dailyq.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;

/**
 * OAuth2 소셜 로그인 실패 시 호출되는 커스텀 핸들러입니다.
 * 소셜 로그인 과정에서 발생하는 예외를 일관된 ProblemDetail JSON 형식으로 응답합니다.
 */
@Slf4j
@Component

public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    // @Qualifier를 사용하여 Spring이 기본으로 등록하는 handlerExceptionResolver를 명시적으로 주입받습니다.
    public OAuth2AuthenticationFailureHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.warn("OAuth2 Login Failed: {}", exception.getMessage(), exception);

        // 직접 응답을 만드는 대신, BusinessException을 생성하여 예외 처리를 위임합니다.
        // 이렇게 하면 ExceptionHandlerAdvice의 handleBusinessException 메소드가 호출되어 일관된 응답을 보낼 수 있습니다.
        handlerExceptionResolver.resolveException(
                request,
                response,
                null,
                new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN, exception.getLocalizedMessage())
        );
    }
}

