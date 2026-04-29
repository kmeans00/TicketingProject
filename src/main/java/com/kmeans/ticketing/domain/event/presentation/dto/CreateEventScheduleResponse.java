package com.kmeans.ticketing.domain.event.presentation.dto;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;

import java.time.LocalDateTime;

/**
 * 회차 등록 응답 DTO
 */
public record CreateEventScheduleResponse(
        Long scheduleId,
        Long eventId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        int totalSeatCount,
        int availableSeatCount
) {
    public static CreateEventScheduleResponse from(EventSchedule schedule) {
        return new CreateEventScheduleResponse(
                schedule.getId(),
                schedule.getEvent().getId(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getTotalSeatCount(),
                schedule.getAvailableSeatCount()
        );
    }
}