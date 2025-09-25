package com.knuissant.dailyq.service.event;

import lombok.Value;

@Value
public class FeedbackCompletedEvent {
    Long feedbackId;
    Long answerId;
}


