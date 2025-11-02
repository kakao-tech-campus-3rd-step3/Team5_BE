package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackContent;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Service
@RequiredArgsConstructor
public class FeedbackUpdateService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public void changeStatusToProcessing(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND, feedbackId));

        if (feedback.getStatus() != FeedbackStatus.PENDING) {
            throw new BusinessException(ErrorCode.FEEDBACK_ALREADY_PROCESSED, feedbackId);
        }
        feedback.startProcessing();
    }

    @Transactional
    public void updateFeedbackSuccess(Long feedbackId, FeedbackContent feedbackContent, long latencyMs) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND, feedbackId));
        feedback.updateSuccess(feedbackContent, latencyMs);
    }

    @Transactional
    public void updateFeedbackFailure(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND, feedbackId));
        feedback.updateFailure();
    }

}
