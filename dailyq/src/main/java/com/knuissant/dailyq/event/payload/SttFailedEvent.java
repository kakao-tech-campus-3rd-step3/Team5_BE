package com.knuissant.dailyq.event.payload;

public record SttFailedEvent(
        Long userId,
        Long answerId,
        String errorMessage
) {

}
