package com.knuissant.dailyq.dto.users;

import jakarta.validation.constraints.NotNull;

// 사용자 직군 선택/수정 요청에 사용되는 DTO
public record UserJobsUpdateRequest(
        @NotNull(message = "직군 ID는 필수입니다.")
        Long jobId
) { }