package com.knuissant.dailyq.dto.admin;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

import com.knuissant.dailyq.domain.jobs.Occupation;


public class OccupationManagementDto {

    @Builder
    public record OccupationResponse(
            Long occupationId,
            String occupationName
    ) {
        public static OccupationResponse from(Occupation occupation) {
            return new OccupationResponse(occupation.getId(), occupation.getName());
        }
    }

    public record OccupationCreateRequest(
            @NotBlank(message = "직군 이름은 필수입니다.")
            String occupationName
    ) {
    }

    public record OccupationUpdateRequest(
            @NotBlank(message = "직군 이름은 필수입니다.")
            String occupationName
    ) {
    }
}