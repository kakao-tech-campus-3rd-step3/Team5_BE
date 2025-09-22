package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final PromptManager promptManager;
    private final FeedbackUpdateService feedbackUpdateService;

    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findByIdWithDetails(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        String systemPrompt = promptManager.load(PromptType.FEEDBACK_SYSTEM);
        String userPrompt = promptManager.load(PromptType.FEEDBACK_USER, question, answer);

        long startTime = System.currentTimeMillis();
        try {
            FeedbackResponse feedbackResponse = gptClient.getFeedback(systemPrompt, userPrompt);
            long latencyMs = System.currentTimeMillis() - startTime;

            feedbackUpdateService.updateFeedbackSuccess(feedbackId, feedbackResponse, latencyMs);
            return feedbackResponse;

        } catch (Exception e) {
            feedbackUpdateService.updateFeedbackFailure(feedbackId);
            throw e;
        }
    }
}
