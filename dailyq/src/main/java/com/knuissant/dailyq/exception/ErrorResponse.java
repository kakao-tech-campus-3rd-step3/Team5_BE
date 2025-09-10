package com.knuissant.dailyq.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String code;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.code = errorCode.name();
    }
}
