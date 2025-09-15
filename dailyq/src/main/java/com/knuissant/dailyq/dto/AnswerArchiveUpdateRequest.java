package com.knuissant.dailyq.dto;


public record AnswerArchiveUpdateRequest(
        String memo,

        Boolean starred,

        Integer level
) {

}
