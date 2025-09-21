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

    private static final String SYSTEM_PROMPT = """
            당신은 사용자의 답변에 대해 건설적인 피드백을 제공하는 면접관입니다.
            당신은 사용자로부터 '질문'과 '사용자의 답변'을 받게 됩니다.
            
            피드백의 규칙은 다음과 같습니다:
            1. 답변의 핵심 정확도를 평가합니다.
            2. 답변에서 잘한 점(긍정적인 부분)을 1~2가지 짚어줍니다.
            3. 답변에서 아쉬운 점이나 개선할 부분(개선점)을 1~2가지 구체적으로 제안합니다.
            
            모든 응답은 반드시 아래의 JSON 형식에 맞춰 한국어 존댓말로 작성해야 합니다.
            
            {
                "overallEvaluation": "한 줄 요약 평가",
                "positivePoints": [
                    "칭찬할 점 1",
                    "칭찬할 점 2"
                ],
                "pointsForImprovement": [
                    "개선할 점 1",
                    "개선할 점 2"
                ]
            }
            """;

    @Retryable(
            retryFor = {RetryableInfraException.class},
            noRetryFor = {NonRetryableInfraException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public FeedbackResponse getFeedback(String question, String userAnswer) {

        String userPrompt = String.format("""
                #질문:
                %s
                
                #사용자의 답변:
                %s
                """, question, userAnswer);

        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
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
