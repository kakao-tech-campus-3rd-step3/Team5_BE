package com.knuissant.dailyq.controller;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
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
import com.knuissant.dailyq.dto.answers.UploadUrlResponse;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.external.ncp.storage.ObjectStorageService;
import com.knuissant.dailyq.service.AnswerCommandService;
import com.knuissant.dailyq.service.AnswerQueryService;

@Validated
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerCommandService answerCommandService;
    private final AnswerQueryService answerQueryService;
    private final ObjectStorageService objectStorageService;

    @GetMapping
    public ResponseEntity<AnswerListResponse.CursorResult<AnswerListResponse.Summary>> getAnswers(
            @AuthenticationPrincipal User principal,
            @ModelAttribute AnswerSearchConditionRequest condition,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") @Positive @Max(50) int limit) {

        Long userId = getUserId(principal);

        if ((lastId == null) != (lastCreatedAt == null)) {
            throw new BusinessException(ErrorCode.INVALID_CURSOR_PARAMETERS);
        }

        //정렬 조건 단일 파라미터 검증
        long filterCount = Stream.of(
                condition.date(),
                condition.jobId(),
                condition.questionType(),
                condition.level(),
                condition.starred()
        ).filter(Objects::nonNull).count();

        Optional.of(filterCount)
                .filter(count -> count <= 1)
                .orElseThrow(() -> new BusinessException(ErrorCode.MULTIPLE_FILTER_NOT_ALLOWED));

        AnswerListResponse.CursorResult<AnswerListResponse.Summary> result = answerQueryService.getArchives(
                userId, condition, lastId, lastCreatedAt, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<AnswerDetailResponse> getAnswerDetail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long answerId) {

        Long userId = getUserId(principal);

        AnswerDetailResponse result = answerQueryService.getAnswerDetail(userId, answerId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity<AnswerArchiveUpdateResponse> updateAnswerDetail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long answerId,
            @RequestBody AnswerArchiveUpdateRequest request
    ) {

        Long userId = getUserId(principal);

        AnswerArchiveUpdateResponse responseDto = answerCommandService.updateAnswer(userId, answerId,
                request);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/upload-url")
    public ResponseEntity<UploadUrlResponse> getUploadUrl(@RequestParam String fileName) {
        return ResponseEntity.ok(objectStorageService.generateUploadUrl(fileName));
    }

    @PostMapping
    public ResponseEntity<AnswerCreateResponse> submitAnswer(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody AnswerCreateRequest request) {

        Long userId = getUserId(principal);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(answerCommandService.submitAnswer(userId, request));
    }

    @PatchMapping("/{answerId}/level")
    public ResponseEntity<AnswerLevelUpdateResponse> updateAnswerLevel(
            @AuthenticationPrincipal User principal,
            @PathVariable Long answerId,
            @Valid @RequestBody AnswerLevelUpdateRequest request) {

        Long userId = getUserId(principal);

        return ResponseEntity.ok(answerCommandService.updateAnswerLevel(userId, answerId, request));
    }

    private Long getUserId(User principal) {
        return Long.parseLong(principal.getUsername());
    }

}
