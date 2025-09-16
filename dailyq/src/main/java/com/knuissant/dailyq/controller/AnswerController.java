package com.knuissant.dailyq.controller;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerArchiveUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerCreateRequest;
import com.knuissant.dailyq.dto.answers.AnswerCreateResponse;
import com.knuissant.dailyq.dto.answers.AnswerDetailResponse;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateRequest;
import com.knuissant.dailyq.dto.answers.AnswerLevelUpdateResponse;
import com.knuissant.dailyq.dto.answers.AnswerListResponse;
import com.knuissant.dailyq.dto.answers.AnswerSearchConditionRequest;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.service.AnswerService;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    public ResponseEntity<AnswerListResponse.CursorResult<AnswerListResponse.Summary>> getAnswers(

            @RequestParam Long userId,

            @ModelAttribute AnswerSearchConditionRequest condition,

            @RequestParam(required = false) String cursor,

            @RequestParam(defaultValue = "10") int limit) {

        //정렬 조건 단일 파라미터 검증
        long filterCount = Stream.of(
                condition.sortOrder(),
                condition.date(),
                condition.jobId(),
                condition.questionType(),
                condition.level(),
                condition.starred()
        ).filter(Objects::nonNull).count();

        Optional.of(filterCount)
                .filter(count -> count <= 1)
                .orElseThrow(() -> new BusinessException(ErrorCode.MULTIPLE_FILTER_NOT_ALLOWED));

        AnswerListResponse.CursorResult<AnswerListResponse.Summary> result = answerService.getArchives(
                userId, condition, cursor, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<AnswerDetailResponse> getAnswerDetail(
            @PathVariable Long answerId) {

        AnswerDetailResponse result = answerService.getAnswerDetail(answerId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity<AnswerArchiveUpdateResponse> updateAnswerDetail(
            @PathVariable Long answerId,
            @RequestBody AnswerArchiveUpdateRequest request) {

        Long userId = 1L; // 임시

        AnswerArchiveUpdateResponse responseDto = answerService.updateAnswer(userId, answerId,
                request);

        return ResponseEntity.ok(responseDto);
    }

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
