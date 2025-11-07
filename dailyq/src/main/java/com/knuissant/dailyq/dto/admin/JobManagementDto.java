package com.knuissant.dailyq.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.knuissant.dailyq.domain.jobs.Job;

public class JobManagementDto {

    public record JobResponse(
            Long jobId,
            String jobName,
            Long occupationId,
            String occupationName
    ) {
        public static JobResponse from(Job job) {
            return new JobResponse(job.getId(), job.getName(), job.getOccupation().getId(), job.getOccupation().getName());
        }
    }

    public record JobCreateRequest(
            @NotBlank(message = "직업 이름은 필수입니다.")
            String jobName,
            @NotNull(message = "상위 직군 ID는 필수입니다.")
            Long occupationId
    ) {
    }

    public record JobUpdateRequest(
            @NotBlank(message = "직업 이름은 필수입니다.")
            String jobName,
            @NotNull(message = "상위 직군 ID는 필수입니다.")
            Long occupationId
    ) {
    }
}