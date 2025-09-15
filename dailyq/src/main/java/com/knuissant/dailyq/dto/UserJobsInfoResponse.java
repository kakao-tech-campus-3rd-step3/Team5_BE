package com.knuissant.dailyq.dto;

import java.util.List;

// 사용자의 직군 정보 및 전체 직군 목록 응답 DTO
public record UserJobsInfoResponse(
        Long selectedJobId,
        List<OccupationResponse> allOccupations
) {
}
