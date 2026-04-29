package com.kmeans.ticketing.domain.event.presentation.dto;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;

import java.time.LocalDateTime;

/**
 * 회차 목록 조회 시 각 회차 1건을 표현하는 응답 DTO
 */
public record EventScheduleSummaryResponse(
        Long scheduleId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalSeatCount,
        int availableSeatCount
) {
    public static EventScheduleSummaryResponse from(EventSchedule schedule) {
        return new EventScheduleSummaryResponse(
                schedule.getId(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getTotalSeatCount(),
                schedule.getAvailableSeatCount()
        );
    }
}