package com.knuissant.dailyq.dto;

import com.knuissant.dailyq.domain.answers.Answer;

public record AnswerArchiveUpdateResponse (
        Boolean starred,

        Integer level,

        String memo
) {
    public static AnswerArchiveUpdateResponse from (Answer answer) {
        return new AnswerArchiveUpdateResponse(answer.getStarred(), answer.getLevel(),
                answer.getMemo());
    }
}
