package com.knuissant.dailyq.event.payload;

public record SttCompletedEvent(
        Long userId,
        Long answerId,
        String answerText
) {

}
