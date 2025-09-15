package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.repository.FeedbackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        FeedbackResponse response;
        long startTime = System.currentTimeMillis();
        try {
            response = gptClient.getFeedback(question, answer);
        } catch (BusinessException e) {
            feedback.updateStatus(FeedbackStatus.FAILED);
            throw e;
        }
        long latencyMs = System.currentTimeMillis() - startTime;

        try {
            feedback.updateContent(objectMapper.writeValueAsString(response));
            feedback.updateLatencyMs(latencyMs);
            feedback.updateStatus(FeedbackStatus.DONE);
        } catch (JsonProcessingException e) {
            feedback.updateStatus(FeedbackStatus.FAILED);
            throw new BusinessException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        return response;
    }
}
