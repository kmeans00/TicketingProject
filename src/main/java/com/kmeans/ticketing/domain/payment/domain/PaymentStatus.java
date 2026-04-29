package com.kmeans.ticketing.domain.payment.domain;

/**
 * 결제 상태
 *
 * READY    : 결제 준비
 * SUCCESS  : 결제 성공
 * FAILED   : 결제 실패
 * CANCELED : 결제 취소
 */
public enum PaymentStatus {
    READY,
    SUCCESS,
    FAILED,
    CANCELED
}