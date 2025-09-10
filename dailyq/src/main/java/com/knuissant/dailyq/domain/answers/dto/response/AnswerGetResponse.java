package com.knuissant.dailyq.domain.answers.dto.response;

import com.knuissant.dailyq.domain.questions.FlowPhase;
import com.knuissant.dailyq.domain.questions.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

public class AnswerGetResponse {

  @Getter
  @Schema(description = "아카이브 목록의 각 아이템 DTO")
  public static class Summary {

    @Schema(description = "답변 ID", example = "1")
    private final Long answerId;

    @Schema(description = "질문 ID", example = "1")
    private final Long questionId;

    @Schema(description = "질문 내용", example = "React의 Virtual DOM을 설명해 주세요.")
    private final String questionText;

    @Schema(description = "질문 유형", example = "TECH")
    private final QuestionType questionType;

    @Schema(description = "면접 플로우 단계", example = "TECH1")
    private final FlowPhase flowPhase;

    @Schema(description = "사용자가 매긴 난이도", example = "4")
    private final Integer level;

    @Schema(description = "즐겨찾기 여부", example = "true")
    private final Boolean starred;

    @Schema(description = "답변 시간", example = "2025-09-05T07:30:00")
    private final LocalDateTime answeredTime;

    public Summary(Long answerId, Long questionId, String questionText, QuestionType questionType,
        FlowPhase flowPhase, Integer level, Boolean starred, LocalDateTime answeredTime) {
      this.answerId = answerId;
      this.questionId = questionId;
      this.questionText = questionText;
      this.questionType = questionType;
      this.flowPhase = flowPhase;
      this.level = level;
      this.starred = starred;
      this.answeredTime = answeredTime;
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
