package com.knuissant.dailyq.dto.questions;

import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.QuestionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RandomQuestionResponse(
    @NotNull Long questionId,
    @NotNull QuestionType questionType,
    FlowPhase flowPhase,
    @NotBlank String questionText,
    Long jobId,
    Integer timeLimitSeconds
) {

    @AssertTrue(message = "flowPhase must be consistent with questionType")
    public boolean isFlowPhaseConsistent() {
        if (flowPhase == null) {
            return true;
        }
        return switch (flowPhase) {
            case INTRO -> questionType == QuestionType.INTRO;
            case MOTIVATION -> questionType == QuestionType.MOTIVATION;
            case PERSONALITY -> questionType == QuestionType.PERSONALITY;
            case TECH1, TECH2 -> questionType == QuestionType.TECH;
        };
    }
}


