package com.knuissant.dailyq.Health.dto;

import java.util.List;
import java.util.Map;

public class HealthDumpResponse {

    private String status;
    private List<Map<String, Object>> rows;

    public HealthDumpResponse(String status, List<Map<String, Object>> rows) {
        this.status = status;
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }
}



