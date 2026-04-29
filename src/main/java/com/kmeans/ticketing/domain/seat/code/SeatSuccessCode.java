package com.kmeans.ticketing.domain.seat.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 좌석 관련 성공 코드
 */
@Getter
public enum SeatSuccessCode implements ApiSuccessCode {

    CREATE_SEATS_SUCCESS(HttpStatus.CREATED, "SEAT_CREATE_SUCCESS", "좌석 등록에 성공했습니다."),
    GET_SEAT_LIST_SUCCESS(HttpStatus.OK, "SEAT_GET_LIST_SUCCESS", "좌석 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SeatSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}