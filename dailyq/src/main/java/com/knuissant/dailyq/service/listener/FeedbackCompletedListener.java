package com.knuissant.dailyq.service.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.service.FollowUpQuestionService;
import com.knuissant.dailyq.service.event.FeedbackCompletedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackCompletedListener {

    private final FollowUpQuestionService followUpQuestionService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleFeedbackCompleted(FeedbackCompletedEvent event) {
        try {
            followUpQuestionService.generateFollowUpQuestions(event.getAnswerId());
            log.info("꼬리질문 생성 완료: {}", event.getAnswerId());
        } catch (Exception ex) {
            throw new InfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR, "꼬리질문 생성 실패", ex);
        }
    }
}


