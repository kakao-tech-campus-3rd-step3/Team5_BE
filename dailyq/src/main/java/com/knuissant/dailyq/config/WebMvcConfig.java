package com.knuissant.dailyq.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    private static final String[] ALLOWED_ORIGIN_PATTERNS = { "*" };
    private static final String[] ALLOWED_METHODS = { "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS" };
    private static final String[] ALLOWED_HEADERS = { "*" };
    private static final long MAX_AGE = 3600;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
                .allowedMethods(ALLOWED_METHODS)
                .allowedHeaders(ALLOWED_HEADERS)
                .allowCredentials(true)
                .maxAge(MAX_AGE); // preflight 캐시 시간 (1시간)
    }
}
