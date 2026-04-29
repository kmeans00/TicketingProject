package com.kmeans.ticketing.domain.settlement.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SettlementSuccessCode implements ApiSuccessCode {

    RUN_DAILY_SETTLEMENT_SUCCESS(HttpStatus.OK, "RUN_DAILY_SETTLEMENT_SUCCESS", "일별 정산 집계에 성공했습니다."),
    GET_DAILY_SETTLEMENT_SUCCESS(HttpStatus.OK, "GET_DAILY_SETTLEMENT_SUCCESS", "일별 정산 조회에 성공했습니다."),
    GET_DAILY_SETTLEMENT_LIST_SUCCESS(HttpStatus.OK, "GET_DAILY_SETTLEMENT_LIST_SUCCESS", "일별 정산 목록 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SettlementSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}