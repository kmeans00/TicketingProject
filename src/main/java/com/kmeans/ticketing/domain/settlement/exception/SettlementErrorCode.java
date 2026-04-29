package com.kmeans.ticketing.domain.settlement.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SettlementErrorCode implements ApiErrorCode {

    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SETTLEMENT_NOT_FOUND", "존재하지 않는 일별 정산입니다."),
    INVALID_SETTLEMENT_RANGE(HttpStatus.BAD_REQUEST, "INVALID_SETTLEMENT_RANGE", "정산 조회 기간이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    SettlementErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}