package com.kmeans.ticketing.domain.reservation.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예매/결제 관련 성공 코드
 */
@Getter
public enum ReservationSuccessCode implements ApiSuccessCode {

    CREATE_RESERVATION_SUCCESS(HttpStatus.CREATED, "RESERVATION_CREATE_SUCCESS", "예매 요청에 성공했습니다."),
    COMPLETE_PAYMENT_SUCCESS(HttpStatus.OK, "PAYMENT_COMPLETE_SUCCESS", "결제 처리에 성공했습니다."),
    CANCEL_RESERVATION_SUCCESS(HttpStatus.OK, "RESERVATION_CANCEL_SUCCESS", "예매 취소에 성공했습니다."),
    GET_MY_RESERVATION_LIST_SUCCESS(HttpStatus.OK, "RESERVATION_GET_MY_LIST_SUCCESS", "내 예매 목록 조회에 성공했습니다."),
    GET_MY_RESERVATION_DETAIL_SUCCESS(HttpStatus.OK, "RESERVATION_GET_MY_DETAIL_SUCCESS", "내 예매 상세 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ReservationSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}