package com.knuissant.dailyq.domain.answers.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "답변 수정 요청 DTO (메모,즐겨찾기,난이도)")
public class AnswerUpdateRequest {

    @Schema(description = "수정할 메모 내용", example = "이 답변은 특히 CS 면접에 중요할 것 같다.")
    private String memo;

    @Schema(description = "수정할 즐겨찾기 상태", example = "true")
    private Boolean starred;

    @Schema(description = "수정할 난이도", example ="3")
    private Integer level;
}
