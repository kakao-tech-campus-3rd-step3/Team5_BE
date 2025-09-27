package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Service
@RequiredArgsConstructor
public class FeedbackUpdateService {

    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void updateFeedbackSuccess(Long feedbackId, FeedbackResponse feedbackResponse, long latencyMs) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        try {
            feedback.updateContent(objectMapper.writeValueAsString(feedbackResponse));
            feedback.updateLatencyMs(latencyMs);
            feedback.updateStatus(FeedbackStatus.DONE);
        } catch (JsonProcessingException e) {
            throw new InfraException(ErrorCode.JSON_PROCESSING_ERROR);
        }
    }

    @Transactional
    public void updateFeedbackFailure(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedback.updateStatus(FeedbackStatus.FAILED);
    }

}
