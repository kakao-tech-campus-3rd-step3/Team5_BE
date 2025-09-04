package com.knuissant.dailyq.Health.controller;

import com.knuissant.dailyq.Health.dto.HealthDumpResponse;
import com.knuissant.dailyq.Health.service.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final DataSource dataSource;
    private final HealthService healthService;

    public HealthController(DataSource dataSource, HealthService healthService) {
        this.dataSource = dataSource;
        this.healthService = healthService;
    }

    @GetMapping("/server")
    public ResponseEntity<String> serverHealth() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/db")
    public ResponseEntity<?> dbHealth() {
        Map<String, Object> body = new HashMap<>();
        try (Connection ignored = dataSource.getConnection()) {
            HealthDumpResponse response = healthService.dumpHealthTable(100);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            body.put("status", "DB_UNAVAILABLE");
            body.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }
}


