package com.knuissant.dailyq.event.payload;

public record SttFailedEvent(
        Long answerId,
        String errorMessage
) {

}
