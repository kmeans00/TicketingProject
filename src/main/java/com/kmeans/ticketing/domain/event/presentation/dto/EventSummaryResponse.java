package com.kmeans.ticketing.domain.event.presentation.dto;

import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventStatus;

import java.time.LocalDateTime;

/**
 * 행사 목록 조회 시 각 행사 1건을 표현하는 응답 DTO
 */
public record EventSummaryResponse(
        Long eventId,
        String title,
        String location,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt,
        EventStatus status
) {
    public static EventSummaryResponse from(Event event) {
        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getBookingOpenAt(),
                event.getBookingCloseAt(),
                event.getStatus()
        );
    }
}