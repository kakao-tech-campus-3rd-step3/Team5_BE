package com.knuissant.dailyq.dto.questions;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.FollowUpQuestion;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionMode;
import com.knuissant.dailyq.domain.questions.QuestionType;

public record RandomQuestionResponse(
    @NotNull Long questionId,
    @NotNull QuestionType questionType,
    FlowPhase flowPhase,
    @NotBlank String questionText,
    Long jobId,
    Integer timeLimitSeconds,
    boolean followUp
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

    /**
     * Question 엔티티로부터 RandomQuestionResponse 생성
     */
    public static RandomQuestionResponse from(Question question, QuestionMode mode, FlowPhase phase, Long jobId, int timeLimitSeconds) {
        return new RandomQuestionResponse(
            question.getId(),
            question.getQuestionType(),
            mode == QuestionMode.FLOW ? phase : null,
            question.getQuestionText(),
            jobId,
            timeLimitSeconds,
            false
        );
    }

    /**
     * FollowUpQuestion 엔티티로부터 RandomQuestionResponse 생성
     */
    public static RandomQuestionResponse fromFollowUp(FollowUpQuestion followUpQuestion, Long jobId, int timeLimitSeconds) {
        return new RandomQuestionResponse(
            followUpQuestion.getId(),
            QuestionType.TECH,  // 꼬리질문은 TECH 타입으로 분류
            null,  // 꼬리질문은 phase 없음
            followUpQuestion.getQuestionText(),
            jobId,
            timeLimitSeconds,
            true
        );
    }
}


