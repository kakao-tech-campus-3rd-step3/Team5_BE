package com.knuissant.dailyq.dto.admin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

import com.knuissant.dailyq.domain.jobs.Job;
import com.knuissant.dailyq.domain.questions.Question;
import com.knuissant.dailyq.domain.questions.QuestionType;

public class QuestionManagementDto {

    @Builder
    public record QuestionResponse(
            Long questionId,
            String questionText,
            QuestionType questionType,
            Boolean enabled
    ) {
        public static QuestionResponse from(Question question) {
            return new QuestionResponse(question.getId(), question.getQuestionText(), question.getQuestionType(), question.getEnabled());
        }
    }

    @Builder
    public record QuestionDetailResponse(
            Long questionId,
            String questionText,
            QuestionType questionType,
            Boolean enabled,
            Set<Long> jobIds
    ) {
        public static QuestionDetailResponse from(Question question) {
            return new QuestionDetailResponse(
                    question.getId(),
                    question.getQuestionText(),
                    question.getQuestionType(),
                    question.getEnabled(),
                    question.getJobs().stream().map(Job::getId).collect(Collectors.toSet())
            );
        }
    }

    public record QuestionCreateRequest(
            @NotBlank(message = "질문 내용은 필수입니다.")
            String questionText,
            @NotNull(message = "질문 타입은 필수입니다.")
            QuestionType questionType,
            @NotEmpty(message = "하나 이상의 직업 ID가 필요합니다.")
            List<Long> jobIds
    ) {
    }

    public record QuestionUpdateRequest(
            @NotBlank(message = "질문 내용은 필수입니다.")
            String questionText,
            @NotNull(message = "질문 타입은 필수입니다.")
            QuestionType questionType,
            @NotNull(message = "활성화 여부는 필수입니다.")
            Boolean enabled,
            @NotEmpty(message = "하나 이상의 직업 ID가 필요합니다.")
            List<Long> jobIds
    ) {
    }
}