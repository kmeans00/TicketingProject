package com.kmeans.ticketing.domain.event.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Event 도메인 성공 코드
 */
@Getter
public enum EventSuccessCode implements ApiSuccessCode {

    CREATE_EVENT_SUCCESS(HttpStatus.CREATED, "EVENT_CREATE_SUCCESS", "행사 등록에 성공했습니다."),
    GET_EVENT_LIST_SUCCESS(HttpStatus.OK, "EVENT_GET_LIST_SUCCESS", "행사 목록 조회에 성공했습니다."),
    GET_EVENT_DETAIL_SUCCESS(HttpStatus.OK, "EVENT_GET_DETAIL_SUCCESS", "행사 상세 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    EventSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}