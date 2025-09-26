package com.knuissant.dailyq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.jwt.JwtAuthenticationFilter;
import com.knuissant.dailyq.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능 비활성화 (JWT 사용 시 불필요)
                .csrf(csrf -> csrf.disable())
                // 세션을 사용하지 않고, STATELESS 상태로 관리
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 테스트를 위해 만든 /login, /home, 그리고 루트(/) 경로는 모두가 접근할 수 있도록 허용합니다.
                        .requestMatchers("/","login", "/home").permitAll()
                        // 소셜 로그인을 시작하는 URL과 토큰 재발급 API도 당연히 허용해야 합니다.
                        .requestMatchers("/oauth2/authorization/**", "/api/v1/token/refresh").permitAll()
                        // 그 외 모든 요청은 인증이 필요합니다.
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 관련 설정
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        // 모든 요청 처리 이전에 JWT 인증 필터를 먼저 실행합니다.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

