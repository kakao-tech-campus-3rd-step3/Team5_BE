package com.knuissant.dailyq.controller;

import com.knuissant.dailyq.dto.AnswerCreateRequest;
import com.knuissant.dailyq.dto.AnswerCreateResponse;
import com.knuissant.dailyq.dto.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // userId 추후 제거
    @PostMapping
    public ResponseEntity<AnswerCreateResponse> submitAnswer(
            @RequestParam("user_id") Long userId,
            @Valid @RequestBody AnswerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(answerService.createAnswerAndFeedback(request, userId));
    }

    @PatchMapping("/{answerId}/level")
    public ResponseEntity<AnswerLevelUpdateResponse> updateAnswerLevel(
            @PathVariable Long answerId,
            @Valid @RequestBody AnswerLevelUpdateRequest request) {
        return ResponseEntity.ok(answerService.updateAnswerLevel(answerId, request));
    }

}
