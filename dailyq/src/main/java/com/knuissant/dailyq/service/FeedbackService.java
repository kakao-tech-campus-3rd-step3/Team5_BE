package com.knuissant.dailyq.service;

import com.knuissant.dailyq.dto.FeedbackRequest;
import com.knuissant.dailyq.dto.FeedbackResponse;
import com.knuissant.dailyq.external.gpt.GptClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final GptClient gptClient;

    public FeedbackResponse generateFeedback(FeedbackRequest request) {

        // questionId로 질문 내용 조회
        // 임시 question
        String question = "Cookie와 Local Storage의 차이점이 무엇인가요?";

        FeedbackResponse feedback = gptClient.getFeedback(question, request.userAnswer());

        // feedback 저장

        return feedback;
    }
}
