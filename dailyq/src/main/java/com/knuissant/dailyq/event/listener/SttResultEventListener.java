package com.knuissant.dailyq.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.event.payload.SttCompletedEvent;
import com.knuissant.dailyq.event.payload.SttFailedEvent;
import com.knuissant.dailyq.service.SseService;

@Component
@RequiredArgsConstructor
public class SttResultEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSttCompleted(SttCompletedEvent event) {
        sseService.sendEvent(event.userId(), "sttCompleted", event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSttFailed(SttFailedEvent event) {
        sseService.sendEvent(event.userId(), "sttFailed", event);
    }
}
