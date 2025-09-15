package com.knuissant.dailyq.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import lombok.Getter;

@Getter
public enum ErrorCode {
  
  VALIDATION_FAILED("VALIDATION_FAILED", "입력값에 대한 유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부에 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  CURSOR_GENERATION_FAILED("CURSOR_GENERATION_FAILED", "커서 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_NOT_FOUND("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MULTIPLE_FILTER_NOT_ALLOWED("MULTIPLE_FILTER_NOT_ALLOWED","조회 필터는 단 하나만 설정할 수 있습니다.",HttpStatus.BAD_REQUEST),
  INVALID_CURSOR("INVALID_CURSOR","잘못된 커서 값입니다.",HttpStatus.BAD_REQUEST),
  ANSWER_NOT_FOUND("ANSWER_NOT_FOUND","해당 답변을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
  FORBIDDEN_ACCESS("FORBIDDEN_ACCESS", "리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
  QUESTION_NOT_FOUND("QUESTION_NOT_FOUND", "해당 질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  FEEDBACK_NOT_FOUND("FEEDBACK_NOT_FOUND", "해당 피드백을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  JSON_PROCESSING_ERROR("JSON_PROCESSING_ERROR", "데이터를 처리하는 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  GPT_API_COMMUNICATION_ERROR("GPT_API_COMMUNICATION_ERROR", "GPT API 통신 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_GPT_RESPONSE("INVALID_GPT_RESPONSE", "AI로부터 유효하지 않은 응답을 받았습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  GPT_RESPONSE_PARSING_FAILED("GPT_RESPONSE_PARSING_FAILED", "AI 응답을 처리하는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  DAILY_LIMIT_REACHED("DAILY_LIMIT_REACHED", "오늘 가능한 질문을 모두 소진했습니다.", HttpStatus.TOO_MANY_REQUESTS),
  NO_QUESTION_AVAILABLE("NO_QUESTION_AVAILABLE", "조건에 맞는 질문이 없습니다.", HttpStatus.NOT_FOUND),
  OCCUPATION_NOT_FOUND("OCCUPATION_NOT_FOUND", "직군을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  JOB_NOT_FOUND("JOB_NOT_FOUND", "직업을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

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
