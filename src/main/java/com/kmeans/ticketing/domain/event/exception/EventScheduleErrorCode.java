package com.kmeans.ticketing.domain.event.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회차 관련 에러 코드
 */
@Getter
public enum EventScheduleErrorCode implements ApiErrorCode {

    EVENT_SCHEDULE_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "EVENT_SCHEDULE_ALREADY_EXISTS",
            "이미 같은 시작 시각의 회차가 존재합니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    EventScheduleErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}