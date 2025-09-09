package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.FeedbackRequest;
import com.knuissant.dailyq.dto.FeedbackResponse;
import com.knuissant.dailyq.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> requestFeedback(@RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.generateFeedback(request));
    }
}
