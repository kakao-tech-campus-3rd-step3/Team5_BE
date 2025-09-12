package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.jobs.Job;
import lombok.Builder;

@Builder
public record JobResponse(
        Long jobId,
        String jobName,
        Long occupationId
) {
    public static JobResponse from(Job job) {
        return new JobResponse(job.getId(), job.getName(), job.getOccupation().getId());
    }
}

