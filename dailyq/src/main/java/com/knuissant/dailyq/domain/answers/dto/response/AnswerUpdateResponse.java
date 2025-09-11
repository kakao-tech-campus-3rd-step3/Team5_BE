package com.knuissant.dailyq.domain.answers.dto.response;

import com.knuissant.dailyq.domain.answers.Answer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답변 수정(즐겨찾기, 난이도) 응답 DTO")
public record AnswerUpdateResponse (
    @Schema(description = "업데이트된 즐찾 상태")
    Boolean starred,

    @Schema(description = "업데이트된 난이도")
    Integer level
    ) {
    public static AnswerUpdateResponse from (Answer answer) {
        return new AnswerUpdateResponse(answer.getStarred(), answer.getLevel());
    }
}
