package com.kmeans.ticketing.domain.event.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회차 관련 성공 코드
 */
@Getter
public enum EventScheduleSuccessCode implements ApiSuccessCode {

    CREATE_EVENT_SCHEDULE_SUCCESS(HttpStatus.CREATED, "EVENT_SCHEDULE_CREATE_SUCCESS", "회차 등록에 성공했습니다."),
    GET_EVENT_SCHEDULE_LIST_SUCCESS(HttpStatus.OK, "EVENT_SCHEDULE_GET_LIST_SUCCESS", "회차 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    EventScheduleSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}