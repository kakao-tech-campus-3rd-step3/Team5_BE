package com.knuissant.dailyq.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String name
) {}
