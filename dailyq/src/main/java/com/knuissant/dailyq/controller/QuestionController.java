package com.knuissant.dailyq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/random")
    public RandomQuestionResponse getRandomQuestion(@RequestParam("user_id") Long userId) {
        return questionService.getRandomQuestion(userId);
    }
}


