package com.knuissant.dailyq.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.event.payload.SttCompletedEvent;
import com.knuissant.dailyq.event.payload.SttFailedEvent;

@Component
@RequiredArgsConstructor
public class SttResultEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSttCompleted(SttCompletedEvent event) {
        // 클라이언트에 성공 결과 푸시
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSttFailed(SttFailedEvent event) {
        // 클라이언트에 실패 결과 푸시
    }
}
