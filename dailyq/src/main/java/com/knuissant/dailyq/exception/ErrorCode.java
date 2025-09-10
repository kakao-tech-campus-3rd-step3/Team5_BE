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
    QUESTION_NOT_FOUND("QUESTION_NOT_FOUND", "해당 질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FEEDBACK_NOT_FOUND("FEEDBACK_NOT_FOUND", "해당 피드백을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    JSON_PROCESSING_ERROR("JSON_PROCESSING_ERROR", "데이터를 처리하는 중 에러가 발생하였습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR),
    GPT_API_COMMUNICATION_ERROR("GPT_API_COMMUNICATION_ERROR", "GPT API 통신 중 에러가 발생하였습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_GPT_RESPONSE("INVALID_GPT_RESPONSE", "AI로부터 유효하지 않은 응답을 받았습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR),
    GPT_RESPONSE_PARSING_FAILED("GPT_RESPONSE_PARSING_FAILED", "AI 응답을 처리하는 중 오류가 발생했습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR);

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
