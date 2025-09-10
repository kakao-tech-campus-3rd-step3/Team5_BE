package com.knuissant.dailyq.domain.answers.dto.request;

import com.knuissant.dailyq.domain.questions.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "아카이브 검색 조건 DTO")
public class AnswerSearchConditionRequest {

    @Schema(description = "푼 날짜", example = "2025-09-09")
    private LocalDate date;

    @Schema(description = "직군 ID(필터링)", example = "1")
    private Long jobId;

    @Schema(description = "질문 유형(필터링)", example = "TECH")
    private QuestionType questionType;

    @Schema(description = "즐겨찾기 여부(필터링)", example = "true")
    private Boolean starred;

    @Schema(description = "난이도(필터링)", example = "3")
    private Integer level;

    @Schema(description = "정렬 순서", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortOrder = "DESC";
}
