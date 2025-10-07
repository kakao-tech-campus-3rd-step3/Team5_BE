package com.knuissant.dailyq.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.feedbacks.CultureFitFeedbackRequest;
import com.knuissant.dailyq.dto.feedbacks.FeedbackResponse;
import com.knuissant.dailyq.service.FeedbackService;
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/{feedbackId}")
    public ResponseEntity<FeedbackResponse> getFeedback(@PathVariable Long feedbackId) {
        return ResponseEntity.ok(feedbackService.generateFeedback(feedbackId));
    }

    @PostMapping("/culturefit")
    public ResponseEntity<Map<String, Long>> requestCultureFitFeedback(
             @RequestBody CultureFitFeedbackRequest request) {

        Long feedbackId = feedbackService.requestCultureFitFeedback(
                request.answerId(),
                request.companyId()
        );

        URI location = URI.create(String.format("/api/feedbacks/%d", feedbackId));
        return ResponseEntity.created(location).body(Map.of("feedbackId", feedbackId));
    }

}
