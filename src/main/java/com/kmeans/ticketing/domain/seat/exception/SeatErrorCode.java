package com.kmeans.ticketing.domain.seat.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 좌석 관련 에러 코드
 */
@Getter
public enum SeatErrorCode implements ApiErrorCode {

    EVENT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_SCHEDULE_NOT_FOUND", "존재하지 않는 회차입니다."),
    SEAT_ALREADY_EXISTS(HttpStatus.CONFLICT, "SEAT_ALREADY_EXISTS", "이미 같은 좌석 번호가 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SeatErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}