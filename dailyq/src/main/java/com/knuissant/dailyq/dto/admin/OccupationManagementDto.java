package com.knuissant.dailyq.dto.admin;

import com.knuissant.dailyq.domain.jobs.Occupation;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

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