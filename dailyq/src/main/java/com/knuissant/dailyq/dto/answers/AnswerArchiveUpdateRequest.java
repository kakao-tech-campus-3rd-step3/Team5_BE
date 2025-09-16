package com.knuissant.dailyq.dto.answers;

public record AnswerArchiveUpdateRequest(
        String memo,

        Boolean starred,

        Integer level
) {

}
