package com.knuissant.dailyq.domain.answers.dto.response;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

public class AnswerListResponse {

    @Schema(description = "아카이브 목록의 각 아이템 DTO")
    public record Summary(
            @Schema(description = "답변 ID", example = "1")
            Long answerId,

            @Schema(description = "질문 ID", example = "1")
            Long questionId,

            @Schema(description = "질문 내용", example = "React의 Virtual DOM을 설명해 주세요.")
            String questionText,

            @Schema(description = "질문 유형", example = "TECH")
            QuestionType questionType,

            @Schema(description = "면접 플로우 단계", example = "TECH1")
            FlowPhase flowPhase,

            @Schema(description = "사용자가 매긴 난이도", example = "4")
            Integer level,

            @Schema(description = "즐겨찾기 여부", example = "true")
            Boolean starred,

            @Schema(description = "답변 시간", example = "2025-09-05T07:30:00")
            LocalDateTime answeredTime
    ) {

        public static Summary from(Answer answer) {
            return new Summary(
                    answer.getId(),
                    answer.getQuestion().getId(),
                    answer.getQuestion().getQuestionText(),
                    answer.getQuestion().getQuestionType(),
                    null, // flow_phase는 보류
                    answer.getLevel(),
                    answer.getStarred(),
                    answer.getAnsweredTime()
            );
        }
    }

    @Schema(description = "커서 기반 페이지네이션 결과 DTO")
    public record CursorResult<T>(
            @Schema(description = "조회된 아이템 목록")
            List<T> items,

            @Schema(description = "다음 페이지 조회를 위한 커서 문자열,다음 페이지가 없으면 null", example = "eyJhbnN3ZXJlZF90aW1lIjoiMjAyNS0wOS0wNVQwNzoyMDowMCIsImFuc3dlcl9pZCI6OTg2fQ==")
            String nextCursor,

            @Schema(description = "다음 페이지 존재 여부", example = "true")
            boolean hasNext
    ) {

    }
}
