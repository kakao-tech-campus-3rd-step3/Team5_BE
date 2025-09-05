package com.knuissant.dailyq.feedback.service;

import com.knuissant.dailyq.feedback.client.GptClient;
import com.knuissant.dailyq.feedback.dto.FeedbackRequest;
import com.knuissant.dailyq.feedback.dto.FeedbackResponse;
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
