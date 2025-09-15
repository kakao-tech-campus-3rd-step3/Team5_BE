package com.knuissant.dailyq.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AnswerCreateRequest(
        @NotNull
        Long questionId,
        String answerText,
        String audioUrl
) {

    @AssertTrue(message = "answerText 또는 audioUrl 중 하나는 반드시 존재해야 합니다.")
    @JsonIgnore
    public boolean isContentPresent() {
        return StringUtils.hasText(answerText) || StringUtils.hasText(audioUrl);
    }

}
