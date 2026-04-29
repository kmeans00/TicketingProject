package com.kmeans.ticketing.global.response;

import org.springframework.http.HttpStatus;

public interface ApiSuccessCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
