package com.knuissant.dailyq.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.knuissant.dailyq.dto.users.JobResponse;
import com.knuissant.dailyq.dto.users.OccupationResponse;
import com.knuissant.dailyq.service.JobService;
import com.knuissant.dailyq.service.OccupationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/occupations")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final OccupationService occupationService;

    @GetMapping
    public ResponseEntity<List<OccupationResponse>> getOccupations() {
        List<OccupationResponse> response = occupationService.findAllOccupations();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{occupationId}/jobs")
    public ResponseEntity<List<JobResponse>> getJobsByOccupation(@PathVariable Long occupationId) {
        List<JobResponse> response = jobService.findJobsByOccupation(occupationId);
        return ResponseEntity.ok(response);
    }
}
