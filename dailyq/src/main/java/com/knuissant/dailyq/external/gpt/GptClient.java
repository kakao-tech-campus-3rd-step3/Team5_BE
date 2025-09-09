package com.knuissant.dailyq.external.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuissant.dailyq.dto.FeedbackResponse;
import com.knuissant.dailyq.external.gpt.dto.GptRequest;
import com.knuissant.dailyq.external.gpt.dto.GptRequest.Message;
import com.knuissant.dailyq.external.gpt.dto.GptRequest.ResponseFormat;
import com.knuissant.dailyq.external.gpt.dto.GptResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GptClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

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

    public FeedbackResponse getFeedback(String question, String userAnswer) {

        String userPrompt = String.format("""
                #질문:
                %s
                
                #사용자의 답변:
                %s
                """, question, userAnswer);

        Message systemMessage = new Message("system", SYSTEM_PROMPT);
        Message requestMessage = new Message("user", userPrompt);

        GptRequest gptRequest = new GptRequest(
                "gpt-5-nano",   // gpt model
                List.of(systemMessage, requestMessage),
                new ResponseFormat("json_object")
        );

        GptResponse gptResponse = webClient.post()
                                           .bodyValue(gptRequest)
                                           .retrieve()
                                           .bodyToMono(GptResponse.class)
                                           .block();

        String jsonContent = Optional.ofNullable(gptResponse)
                                     .flatMap(GptResponse::getFirstMessageContent)
                                     .filter(a -> !a.isEmpty())
                                     .orElseThrow(() -> new RuntimeException("답변 생성 실패"));

        try {
            return objectMapper.readValue(jsonContent, FeedbackResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답을 파싱 실패", e);
        }
    }

}
