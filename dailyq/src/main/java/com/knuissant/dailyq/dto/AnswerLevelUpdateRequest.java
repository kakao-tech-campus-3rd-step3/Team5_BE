package com.knuissant.dailyq.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AnswerLevelUpdateRequest(
        @NotNull
        @Min(value = 1)
        @Max(value = 5)
        Integer level
) {

}
