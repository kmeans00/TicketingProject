package com.kmeans.ticketing.domain.seat.presentation.dto;

/**
 * 좌석 일괄 등록 응답 DTO
 */
public record CreateSeatsResponse(
        Long scheduleId,
        int createdCount
) {
}