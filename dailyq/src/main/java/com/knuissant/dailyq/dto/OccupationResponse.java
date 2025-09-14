package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.jobs.Occupation;

public record OccupationResponse(
        Long occupationId,
        String occupationName
) {
    public static OccupationResponse from(Occupation occupation) {
        return new OccupationResponse(occupation.getId(), occupation.getName());
    }
}
