package com.kmeans.ticketing.domain.event.application.dto;

import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventStatus;

import java.time.LocalDateTime;

/**
 * 행사 등록 처리 결과를 담는 application 계층용 DTO
 */
public record CreateEventResult(
        Long eventId,
        String title,
        String description,
        String location,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt,
        EventStatus status
) {
    public static CreateEventResult from(Event event) {
        return new CreateEventResult(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getBookingOpenAt(),
                event.getBookingCloseAt(),
                event.getStatus()
        );
    }
}