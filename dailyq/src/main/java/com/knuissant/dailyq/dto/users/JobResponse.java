package com.knuissant.dailyq.dto.users;

import com.knuissant.dailyq.domain.jobs.Job;

public record JobResponse(
        Long jobId,
        String jobName,
        Long occupationId
) {
    public static JobResponse from(Job job) {
        return new JobResponse(job.getId(), job.getName(), job.getOccupation().getId());
    }
}

