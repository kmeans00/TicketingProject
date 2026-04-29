package com.kmeans.ticketing.domain.event.domain;

/**
 * 행사 상태
 *
 * READY    : 행사 생성은 되었지만 아직 예매 오픈 전
 * OPEN     : 현재 예매 가능한 상태
 * CLOSED   : 예매 마감
 * CANCELED : 행사 취소
 */
public enum EventStatus {
    READY,
    OPEN,
    CLOSED,
    CANCELED
}