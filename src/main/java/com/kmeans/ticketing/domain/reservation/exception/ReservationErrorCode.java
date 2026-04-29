package com.kmeans.ticketing.domain.reservation.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예매/결제 관련 에러 코드
 */
@Getter
public enum ReservationErrorCode implements ApiErrorCode {

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예매입니다."),
    SEAT_NOT_AVAILABLE(HttpStatus.CONFLICT, "SEAT_NOT_AVAILABLE", "이미 예매 불가능한 좌석입니다."),
    SEAT_RESERVATION_CONFLICT(HttpStatus.CONFLICT, "SEAT_RESERVATION_CONFLICT", "다른 사용자가 먼저 선택한 좌석입니다. 다시 시도해주세요."),
    EVENT_SCHEDULE_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "EVENT_SCHEDULE_ALREADY_STARTED", "이미 시작된 회차는 예매할 수 없습니다."),
    RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST, "RESERVATION_EXPIRED", "만료된 예매입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제 정보를 찾을 수 없습니다."),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RESERVATION_ACCESS_DENIED", "본인 예매만 접근할 수 있습니다."),
    RESERVATION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "RESERVATION_ALREADY_PROCESSED", "이미 처리된 예매입니다."),
    RESERVATION_CANCEL_NOT_ALLOWED(HttpStatus.CONFLICT, "RESERVATION_CANCEL_NOT_ALLOWED", "취소할 수 없는 예매 상태입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ReservationErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}