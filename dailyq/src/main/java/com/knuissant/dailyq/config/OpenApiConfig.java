package com.knuissant.dailyq.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("DailyQ API")
                        .description("DailyQ 백엔드 API 문서")
                        .version("1.0.0"));

        // 프로덕션 환경에서는 HTTPS 서버 정보 추가
        if ("prod".equals(activeProfile)) {
            openAPI.servers(List.of(
                    new Server()
                            .url("https://dailyq.my")
                            .description("Production Server")
            ));
        } else {
            // 개발 환경에서는 HTTP 서버 정보 추가
            openAPI.servers(List.of(
                    new Server()
                            .url("http://localhost:" + serverPort)
                            .description("Development Server")
            ));
        }

        return openAPI;
    }
}
