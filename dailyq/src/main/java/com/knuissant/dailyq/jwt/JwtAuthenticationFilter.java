package com.knuissant.dailyq.jwt;

import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.exception.ErrorCode;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    // JSON 응답을 만들기 위해 ObjectMapper를 주입받습니다.
    private final ObjectMapper objectMapper;

    /**
     * 모든 요청이 컨트롤러에 도달하기 전에 이 필터를 거칩니다.
     * JWT 토큰의 유효성을 검사하고, 예외 발생 시 표준화된 에러 응답을 생성합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 요청에서 토큰을 추출합니다.
            String token = resolveToken(request);

            // 2. 토큰이 존재하고 유효하다면, 인증 정보를 SecurityContext에 저장합니다.
            if (token != null && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 3. 다음 필터로 요청을 전달합니다.
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token detected: {}", e.getMessage());
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // 유효하지 않은 토큰(형식 오류, 서명 오류 등)을 한 번에 처리합니다.
            log.warn("Invalid JWT token detected: {}", e.getMessage());
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } catch (JwtException e) {
            // 기타 모든 JWT 관련 예외를 처리합니다.
            log.warn("JWT error detected: {}", e.getMessage());
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 요청에서 토큰 정보를 추출하는 메소드입니다.
     * @param request 들어온 HttpServletRequest
     * @return 추출된 토큰 문자열 (없으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        // [1순위] URL 쿼리 파라미터에서 'token'을 찾습니다.
        String tokenFromParam = request.getParameter("token");
        if (StringUtils.hasText(tokenFromParam)) {
            return tokenFromParam;
        }

        // [2순위] HTTP Authorization 헤더에서 'Bearer' 토큰을 찾습니다.
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * 표준화된 에러 응답(ProblemDetail)을 생성하여 클라이언트에게 전송하는 헬퍼 메소드입니다.
     * @param response HttpServletResponse 객체
     * @param errorCode 사용할 ErrorCode
     * @throws IOException
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ProblemDetail problemDetail = errorCode.toProblemDetail();
        String jsonResponse = objectMapper.writeValueAsString(problemDetail);

        response.getWriter().write(jsonResponse);
    }
}
