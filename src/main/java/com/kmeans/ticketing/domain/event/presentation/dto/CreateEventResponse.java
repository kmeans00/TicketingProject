package com.kmeans.ticketing.domain.event.presentation.dto;

import com.kmeans.ticketing.domain.event.application.dto.CreateEventResult;
import com.kmeans.ticketing.domain.event.domain.EventStatus;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 내려줄 행사 등록 응답 DTO
 */
public record CreateEventResponse(
        Long eventId,
        String title,
        String description,
        String location,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt,
        EventStatus status
) {
    public static CreateEventResponse from(CreateEventResult result) {
        return new CreateEventResponse(
                result.eventId(),
                result.title(),
                result.description(),
                result.location(),
                result.bookingOpenAt(),
                result.bookingCloseAt(),
                result.status()
        );
    }
}