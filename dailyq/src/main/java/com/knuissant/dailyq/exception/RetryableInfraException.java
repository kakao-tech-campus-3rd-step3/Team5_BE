package com.knuissant.dailyq.exception;

public class RetryableInfraException extends InfraException {

    public RetryableInfraException(ErrorCode errorCode) {
        super(errorCode);
    }
}
