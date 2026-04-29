package com.kmeans.ticketing.domain.seat.domain;

/**
 * 좌석 상태
 *
 * AVAILABLE : 예매 가능
 * RESERVED  : 결제 대기 중으로 임시 선점된 상태
 * SOLD      : 결제 완료된 상태
 * BLOCKED   : 관리자에 의해 판매 불가 처리된 상태
 */
public enum SeatStatus {
    AVAILABLE,
    RESERVED,
    SOLD,
    BLOCKED
}