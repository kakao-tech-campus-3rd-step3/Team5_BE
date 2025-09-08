package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/random")
    public RandomQuestionResponse getRandomQuestion(
        @RequestParam("user_id") Long userId,
        @RequestParam(value = "mode", required = false) String mode,
        @RequestParam(value = "phase", required = false) String phase,
        @RequestParam(value = "job_id", required = false) Long jobId
    ) {
        return questionService.getRandomQuestion(userId, mode, phase, jobId);
    }
}


