package com.knuissant.dailyq.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 4xx Business Errors
    // Bad Request(400)
    VALIDATION_FAILED("VALIDATION_FAILED", "입력값에 대한 유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
    MULTIPLE_FILTER_NOT_ALLOWED("MULTIPLE_FILTER_NOT_ALLOWED", "조회 필터는 단 하나만 설정할 수 있습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_RIVAL_YOURSELF("CANNOT_RIVAL_YOURSELF", "자기 자신에게는 라이벌 신청을 할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Not Found(404)
    USER_NOT_FOUND("USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND("ANSWER_NOT_FOUND", "해당 답변을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND("QUESTION_NOT_FOUND", "해당 질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FEEDBACK_NOT_FOUND("FEEDBACK_NOT_FOUND", "해당 피드백을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NO_QUESTION_AVAILABLE("NO_QUESTION_AVAILABLE", "조건에 맞는 질문이 없습니다.", HttpStatus.NOT_FOUND),
    OCCUPATION_NOT_FOUND("OCCUPATION_NOT_FOUND", "직군을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    JOB_NOT_FOUND("JOB_NOT_FOUND", "직업을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RIVAL_RELATION_NOT_FOUND("RIVAL_RELATION_NOT_FOUND", "라이벌 관계를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    // etc 4xx
    FORBIDDEN_ACCESS("FORBIDDEN_ACCESS", "리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    DAILY_LIMIT_REACHED("DAILY_LIMIT_REACHED", "오늘 가능한 질문을 모두 소진했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    ALREADY_FOLLOWING_RIVAL("ALREADY_FOLLOWING_RIVAL", "이미 팔로우 중인 라이벌입니다.", HttpStatus.CONFLICT),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_SOCIAL_LOGIN("INVALID_SOCIAL_LOGIN", "잘못된 소셜 로그인 요청입니다.", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED("LOGIN_FAILED", "로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "인증되지 않은 접근입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "잘못된 인증 정보입니다.", HttpStatus.UNAUTHORIZED),
    FEEDBACK_ALREADY_PROCESSED("FEEDBACK_ALREADY_PROCESSED", "이미 처리 중인 피드백입니다.", HttpStatus.CONFLICT),
    //5xx System Errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부에 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    CURSOR_GENERATION_FAILED("CURSOR_GENERATION_FAILED", "커서 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PROCESSING_ERROR("JSON_PROCESSING_ERROR", "데이터를 처리하는 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    GPT_API_COMMUNICATION_ERROR("GPT_API_COMMUNICATION_ERROR", "GPT API 통신 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_IO_ERROR("FILE_IO_ERROR", "파일 처리 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_JOB_NOT_SET("USER_JOB_NOT_SET", "사용자 직무 정보가 설정되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_PREFERENCES_NOT_FOUND("USER_PREFERENCES_NOT_FOUND", "사용자 설정 정보를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_FLOW_PROGRESS_NOT_FOUND("USER_FLOW_PROGRESS_NOT_FOUND", "사용자 플로우 진행 상태를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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