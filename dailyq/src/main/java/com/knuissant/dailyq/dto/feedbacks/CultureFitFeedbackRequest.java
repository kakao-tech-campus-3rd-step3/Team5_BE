package com.knuissant.dailyq.dto.feedbacks;

import jakarta.validation.constraints.NotNull;

public record CultureFitFeedbackRequest(
        @NotNull Long answerId,
        @NotNull Long companyId
) {

}
