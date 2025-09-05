package com.knuissant.dailyq.Question.dto;

import com.knuissant.dailyq.Question.enums.JobRole;
import com.knuissant.dailyq.Question.enums.Mode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DailyQuestionRequest {

    @NotNull
    private Mode mode;

    @NotNull
    private JobRole jobRole;

    @Min(1)
    @Max(20)
    private Integer count; // nullable -> default 1

    @NotNull
    @Min(1)
    private Long userId;

    public Mode getMode() { return mode; }
    public JobRole getJobRole() { return jobRole; }
    public Integer getCount() { return count; }
    public Long getUserId() { return userId; }
}


