package com.knuissant.dailyq.dto.answers;

import com.knuissant.dailyq.domain.answers.Answer;

public record AnswerLevelUpdateResponse(
        Long answerId,
        Integer level
) {

    public static AnswerLevelUpdateResponse from(Answer answer) {
        return new AnswerLevelUpdateResponse(
                answer.getId(),
                answer.getLevel()
        );
    }

}
