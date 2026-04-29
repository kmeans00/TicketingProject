package com.kmeans.ticketing.global.exception;

import org.springframework.http.HttpStatus;

public interface ApiErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}