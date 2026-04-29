package com.kmeans.ticketing.domain.reservation.domain;

/**
 * 예매 상태
 *
 * PENDING_PAYMENT : 결제 대기
 * CONFIRMED       : 결제 완료, 예매 확정
 * CANCELED        : 사용자 취소
 * EXPIRED         : 결제 기한 만료
 */
public enum ReservationStatus {
    PENDING_PAYMENT,
    CONFIRMED,
    CANCELED,
    EXPIRED
}