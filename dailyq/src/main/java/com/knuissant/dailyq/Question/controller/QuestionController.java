package com.knuissant.dailyq.Question.controller;

import com.knuissant.dailyq.Question.dto.DailyQuestionRequest;
import com.knuissant.dailyq.Question.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/daily")
    public ResponseEntity<?> getDaily(@Valid @RequestBody DailyQuestionRequest request) {
        return questionService.getDailyQuestions(request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}


