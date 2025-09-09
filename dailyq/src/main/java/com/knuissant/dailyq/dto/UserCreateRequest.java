package com.knuissant.dailyq.dto;

import lombok.Builder;

// 사용자 생성 요청에 사용되는 DTO
@Builder
public record UserCreateRequest(
        String email,
        String name
) {}