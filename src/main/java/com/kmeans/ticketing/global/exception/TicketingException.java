package com.kmeans.ticketing.global.exception;

import lombok.Getter;

@Getter
public class TicketingException extends RuntimeException {

    private final ApiErrorCode errorCode;

    public TicketingException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}