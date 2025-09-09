package com.knuissant.dailyq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Swagger 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 테스트 API 엔드포인트
     * @return 테스트 메시지
     */
    @GetMapping()
    public String healthCheck() {
        return "DailyQ Backend API is running";
    }
}
