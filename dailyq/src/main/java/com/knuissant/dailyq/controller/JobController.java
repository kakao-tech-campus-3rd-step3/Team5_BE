package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.JobResponse;
import com.knuissant.dailyq.dto.OccupationResponse;
import com.knuissant.dailyq.service.JobService;
import com.knuissant.dailyq.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final OccupationService occupationService;

    @GetMapping("/occupations")
    public ResponseEntity<List<OccupationResponse>> getOccupations() {
        List<OccupationResponse> response = occupationService.findAllOccupations();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/occupations/{occupationId}/jobs")
    public ResponseEntity<List<JobResponse>> getJobsByOccupation(@PathVariable Long occupationId) {
        List<JobResponse> response = jobService.findJobsByOccupation(occupationId);
        return ResponseEntity.ok(response);
    }
}