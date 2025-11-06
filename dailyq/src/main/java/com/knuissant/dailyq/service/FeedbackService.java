package com.knuissant.dailyq.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.answers.Answer;
import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.domain.feedbacks.FeedbackContent;
import com.knuissant.dailyq.domain.feedbacks.FeedbackStatus;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.FeedbackRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final PromptManager promptManager;
    private final FeedbackUpdateService feedbackUpdateService;

    @Transactional
    public Feedback createPendingFeedback(Answer answer) {

        Feedback feedback = Feedback.create(answer, FeedbackStatus.PENDING);
        return feedbackRepository.save(feedback);
    }

    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findWithDetailsById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND, feedbackId));

        boolean followUp = (feedback.getAnswer() != null && feedback.getAnswer().getFollowUpQuestion() != null);

        if (feedback.isDone()) {
            return FeedbackResponse.of(feedback, followUp);
        }

        feedbackUpdateService.changeStatusToProcessing(feedbackId);

        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        String systemPrompt = promptManager.load(PromptType.FEEDBACK_SYSTEM);
        String userPrompt = promptManager.load(PromptType.FEEDBACK_USER, question, answer);

        long startTime = System.currentTimeMillis();
        try {
            FeedbackContent feedbackContent = gptClient.call(systemPrompt, userPrompt);
            long latencyMs = System.currentTimeMillis() - startTime;

            Feedback updatedFeedback = feedbackUpdateService.updateFeedbackSuccess(feedbackId, feedbackContent, latencyMs);

            return FeedbackResponse.of(updatedFeedback, followUp);

        } catch (Exception e) {
            try {
                feedbackUpdateService.updateFeedbackFailure(feedbackId);
            } catch (Exception failureUpdateEx) {
                log.error("Failed to update status to FAILED for feedbackId {}, Original error: {}",
                        feedbackId, e.getMessage());
            }
            throw e;
        }
    }
}
