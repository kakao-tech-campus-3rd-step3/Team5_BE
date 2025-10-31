package com.knuissant.dailyq.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.questions.FollowUpGenerationResponse;
import com.knuissant.dailyq.dto.questions.RandomQuestionResponse;
import com.knuissant.dailyq.service.FollowUpQuestionService;
import com.knuissant.dailyq.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final FollowUpQuestionService followUpQuestionService;

    @GetMapping("/random")
    public RandomQuestionResponse getRandomQuestion(@AuthenticationPrincipal User principal) {
        Long userId = Long.parseLong(principal.getUsername());
        return questionService.getRandomQuestion(userId);
    }

    @PostMapping("/followUp/{answerId}")
    public FollowUpGenerationResponse generateFollowUpQuestions(
            @PathVariable(name = "answerId") Long answerId,
            @AuthenticationPrincipal User principal) {
        Long userId = Long.parseLong(principal.getUsername());
        int count = followUpQuestionService.generateFollowUpQuestions(answerId, userId);
        return FollowUpGenerationResponse.of(count);
    }
}


