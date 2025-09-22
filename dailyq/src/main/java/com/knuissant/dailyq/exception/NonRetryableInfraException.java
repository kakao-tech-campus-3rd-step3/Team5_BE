package com.knuissant.dailyq.exception;

public class NonRetryableInfraException extends InfraException {

    public NonRetryableInfraException(ErrorCode errorCode) {
        super(errorCode);
    }

}
