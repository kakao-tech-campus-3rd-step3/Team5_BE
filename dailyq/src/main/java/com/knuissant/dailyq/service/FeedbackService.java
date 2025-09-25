package com.knuissant.dailyq.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.feedbacks.Feedback;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;
import com.knuissant.dailyq.external.gpt.GptClient;
import com.knuissant.dailyq.external.gpt.PromptManager;
import com.knuissant.dailyq.external.gpt.PromptType;
import com.knuissant.dailyq.repository.FeedbackRepository;
import com.knuissant.dailyq.service.event.FeedbackCompletedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;
    private final FeedbackRepository feedbackRepository;
    private final PromptManager promptManager;
    private final FeedbackUpdateService feedbackUpdateService;
    private final ApplicationEventPublisher eventPublisher;

    public FeedbackResponse generateFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findWithDetailsById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));
        String question = feedback.getAnswer().getQuestion().getQuestionText();
        String answer = feedback.getAnswer().getAnswerText();

        String systemPrompt = promptManager.load(PromptType.FEEDBACK_SYSTEM);
        String userPrompt = promptManager.load(PromptType.FEEDBACK_USER, question, answer);

        long startTime = System.currentTimeMillis();
        try {
            FeedbackResponse feedbackResponse = gptClient.call(systemPrompt, userPrompt);
            long latencyMs = System.currentTimeMillis() - startTime;

            feedbackUpdateService.updateFeedbackSuccess(feedbackId, feedbackResponse, latencyMs);
            
            // 응답 반환을 우선: 이벤트 발행 (커밋 후 리스너에서 비동기 처리)
            if (feedback.getAnswer().getFollowUpQuestion() == null) {
                log.info("Publishing FeedbackCompletedEvent for answerId: {}", feedback.getAnswer().getId());
                eventPublisher.publishEvent(new FeedbackCompletedEvent(feedback.getId(), feedback.getAnswer().getId()));
            } else {
                log.info("[FollowUp] Skip generating follow-ups: answer {} is a follow-up answer", feedback.getAnswer().getId());
            }
            
            return feedbackResponse;

        } catch (Exception e) {
            try {
                feedbackUpdateService.updateFeedbackFailure(feedbackId);
            } catch (Exception failureUpdateEx) {
                throw new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "피드백 상태 업데이트 실패", failureUpdateEx);
            }
            throw new InfraException(ErrorCode.INTERNAL_SERVER_ERROR, "피드백 생성 처리 중 오류", e);
        }
    }
}
