package com.kmeans.ticketing.domain.event.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Event 도메인 에러 코드
 */
@Getter
public enum EventErrorCode implements ApiErrorCode {

    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND", "존재하지 않는 행사입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    EventErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}