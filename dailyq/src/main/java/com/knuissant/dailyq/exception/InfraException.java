package com.knuissant.dailyq.exception;

import lombok.Getter;

@Getter
public class InfraException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public InfraException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    public InfraException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}
