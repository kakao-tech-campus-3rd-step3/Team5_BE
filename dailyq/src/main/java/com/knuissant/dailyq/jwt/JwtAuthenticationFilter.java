package com.knuissant.dailyq.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    /**
     * 모든 요청이 컨트롤러에 도달하기 전에 이 필터를 거칩니다.
     * JWT 토큰의 유효성을 검사하는 핵심 로직입니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청(Request)에서 토큰을 추출합니다. (이제 이 메소드는 헤더와 URL 파라미터를 모두 확인합니다.)
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효하다면,
        if (token != null && tokenProvider.validateToken(token)) {
            // 3. 토큰에서 인증 정보(Authentication)를 가져옵니다.
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 4. SecurityContextHolder에 이 인증 정보를 저장합니다.
            //    이렇게 저장하면, Spring Security는 이 요청을 "인증된 사용자"의 요청으로 인식하게 됩니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 토큰 정보를 추출하는 메소드입니다.
     * @param request 들어온 HttpServletRequest
     * @return 추출된 토큰 문자열 (없으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        // [1순위] URL 쿼리 파라미터에서 'token'을 찾습니다.
        // 이것은 소셜 로그인 성공 직후, /home?token=... 형태로 리디렉션될 때를 위한 처리입니다.
        String tokenFromParam = request.getParameter("token");
        if (StringUtils.hasText(tokenFromParam)) {
            return tokenFromParam;
        }

        // [2순위] HTTP Authorization 헤더에서 'Bearer' 토큰을 찾습니다.
        // 이것은 로그인 성공 후, 클라이언트(웹/앱)가 API를 호출할 때 사용하는 표준 방식입니다.
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

