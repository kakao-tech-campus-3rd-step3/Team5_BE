package com.knuissant.dailyq.dto;

import lombok.Builder;

import java.util.List;

// 사용자 직군 선택/수정 요청에 사용되는 DTO
@Builder
public record UserJobsUpdateRequest(
        Long jobId
) {}
