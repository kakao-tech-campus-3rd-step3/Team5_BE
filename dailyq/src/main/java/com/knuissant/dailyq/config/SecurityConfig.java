package com.knuissant.dailyq.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;
    
    @Value("${security.hsts.enabled:false}")
    private boolean hstsEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정을 SecurityFilterChain에 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF 보호 기능 비활성화 (JWT 사용 시 불필요)
                .csrf(csrf -> csrf.disable())
                // 세션을 사용하지 않고, STATELESS 상태로 관리
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        //preflight 처리를 위해 OPTIONS Method 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 소셜 로그인을 시작하는 URL과 토큰 재발급 API도 당연히 허용해야 합니다.
                        .requestMatchers("/oauth2/authorization/**", "/api/token/refresh",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/login/oauth2/**",
                                "/api/dev/**").permitAll()
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
        
        // HSTS 설정 (Production 환경에서만 활성화)
        // 모든 서브도메인이 HTTPS를 지원하는 경우에만 includeSubDomains(true) 사용
        if (hstsEnabled) {
            http.headers(headers -> headers
                    .httpStrictTransportSecurity(hsts -> hsts
                            .maxAgeInSeconds(31536000)  // 1년
                            .includeSubDomains(true)    // 서브도메인 포함
                    )
            );
        }

        // 모든 요청 처리 이전에 JWT 인증 필터를 먼저 실행합니다.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        // 모든 Method 허용
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 모든 헤더 허용 (우리는 Authorization)
        configuration.setAllowedHeaders(List.of("*"));
        // 쿠키 허용
        configuration.setAllowCredentials(true);
        // Preflight 요청 결과 캐시 시간 설정 max : 3600L(1h)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 CORS 정책 허용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

