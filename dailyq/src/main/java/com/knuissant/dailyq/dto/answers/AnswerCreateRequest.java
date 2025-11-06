package com.knuissant.dailyq.dto.answers;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AnswerCreateRequest(
        @NotNull
        Long questionId,
        String answerText,
        String audioUrl,
        boolean followUp
) {

    @AssertTrue(message = "answerText 또는 audioUrl 중 정확히 하나만 존재해야 합니다.")
    @JsonIgnore
    public boolean isContentPresent() {
        boolean isTextPresent = StringUtils.hasText(answerText);
        boolean isAudioPresent = StringUtils.hasText(audioUrl);

        return (isTextPresent && !isAudioPresent) || (!isTextPresent && isAudioPresent);
    }

}
