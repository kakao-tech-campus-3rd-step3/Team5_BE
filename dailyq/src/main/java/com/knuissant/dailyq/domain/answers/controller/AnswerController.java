package com.knuissant.dailyq.domain.answers.controller;

import com.knuissant.dailyq.domain.answers.dto.response.AnswerDetailResponse;
import com.knuissant.dailyq.domain.answers.dto.response.AnswerGetResponse;
import com.knuissant.dailyq.domain.answers.dto.request.AnswerSearchConditionRequest;
import com.knuissant.dailyq.domain.answers.service.AnswerService;
import com.knuissant.dailyq.exception.BusinessException;
import com.knuissant.dailyq.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Archive", description = "아카이브 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnswerController {

  private final AnswerService answerService;

  @Operation(summary = "아카이브 조회", description = "사용자의 답변(질문) 목록을 커서 기반 조회")
  @GetMapping("/answers")
  public ResponseEntity<AnswerGetResponse.CursorResult<AnswerGetResponse.Summary>> getAnswers(

      @Parameter(description = "사용자 ID(임시)", required = true, example = "1")
      @RequestParam Long userId,

      @ModelAttribute AnswerSearchConditionRequest condition,

      @Parameter(description = "다음 페이지 조회를 위한 커서,첫 페이지 조회 시 생략")
      @RequestParam(required = false) String cursor,

      @Parameter(description = "한 페이지의 아이템 개수")
      @RequestParam(defaultValue = "10") int limit) {

    //정렬 조건 단일 파라미터 검증
    long filterCount = Stream.of(
        condition.getDate(),
        condition.getJobId(),
        condition.getQuestionType(),
        condition.getLevel(),
        condition.getStarred()
    ).filter(Objects::nonNull).count();

    if (filterCount > 1) {
      throw new BusinessException(ErrorCode.MULTIPLE_FILTER_NOT_ALLOWED);
    }

    AnswerGetResponse.CursorResult<AnswerGetResponse.Summary> result = answerService.getArchives(userId, condition, cursor, limit);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "아카이브 질문 상세 조회", description = "사용자의 질문,답변,피드백 정보를 상세 조회")
  @GetMapping("/answers/{answerId}")
  public ResponseEntity<AnswerDetailResponse> getAnswerDetail(
      @Parameter(description = "답변 ID", required = true, example = "1")
      @PathVariable Long answerId) {

    AnswerDetailResponse result = answerService.getAnswerDetail(answerId);

    return ResponseEntity.ok(result);
  }
}
