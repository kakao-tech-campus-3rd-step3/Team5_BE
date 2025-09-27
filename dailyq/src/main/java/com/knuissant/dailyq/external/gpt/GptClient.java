package com.knuissant.dailyq.external.gpt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.NonRetryableInfraException;
import com.knuissant.dailyq.exception.RetryableInfraException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptClient {

    private final ChatClient chatClient;

    @Retryable(
            retryFor = {RetryableInfraException.class},
            noRetryFor = {NonRetryableInfraException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public FeedbackResponse call(String systemPrompt, String userPrompt) {

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(FeedbackResponse.class);
        } catch (NonTransientAiException e) {
            log.error("[GPT] Non-transient error (handled by Spring AI retry): {}", e.getMessage());
            throw new NonRetryableInfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR);
        } catch (TransientAiException e) {
            log.error("[GPT] Transient error (handled by Spring AI retry): {}", e.getMessage());
            throw new NonRetryableInfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR);
        } catch (ResourceAccessException e) {
            log.error("[GPT] Network error: {}", e.getMessage());
            throw new RetryableInfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR);
        } catch (Exception e) {
            log.error("[GPT] Unexpected error: {}", e.getMessage());
            throw new NonRetryableInfraException(ErrorCode.GPT_API_COMMUNICATION_ERROR);
        }
    }

}
