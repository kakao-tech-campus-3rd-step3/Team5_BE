package com.knuissant.dailyq.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
public enum ErrorCode {

  VALIDATION_FAILED("VALIDATION_FAILED", "입력값에 대한 유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부에 에러가 발생하였습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),
  USER_NOT_FOUND("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DAILY_LIMIT_REACHED("DAILY_LIMIT_REACHED", "오늘 가능한 질문을 모두 소진했습니다.", HttpStatus.TOO_MANY_REQUESTS),
  NO_QUESTION_AVAILABLE("NO_QUESTION_AVAILABLE", "조건에 맞는 질문이 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }

  public ProblemDetail toProblemDetail() {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(this.status, this.message);
    problemDetail.setProperty("code", this.code);
    return problemDetail;
  }
}
