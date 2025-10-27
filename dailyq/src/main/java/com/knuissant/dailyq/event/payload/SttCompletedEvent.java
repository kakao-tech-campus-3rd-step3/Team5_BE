package com.knuissant.dailyq.event.payload;

public record SttCompletedEvent(
        Long answerId,
        String answerText
) {

}
