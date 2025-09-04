package com.knuissant.dailyq.Health.service;

import com.knuissant.dailyq.Health.dto.HealthDumpResponse;
import com.knuissant.dailyq.Health.repository.HealthRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HealthService {

    private final HealthRepository healthRepository;

    public HealthService(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    public HealthDumpResponse dumpHealthTable(int limit) {
        List<Map<String, Object>> rows = healthRepository.findHealthDump(limit);
        return new HealthDumpResponse("OK", rows);
    }
}


