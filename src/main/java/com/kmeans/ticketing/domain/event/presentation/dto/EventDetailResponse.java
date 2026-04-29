package com.kmeans.ticketing.domain.event.presentation.dto;

import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventStatus;

import java.time.LocalDateTime;

/**
 * 행사 상세 조회 응답 DTO
 *
 * 목록 조회와 달리 상세에서는 description까지 내려준다.
 */
public record EventDetailResponse(
        Long eventId,
        String title,
        String description,
        String location,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt,
        EventStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EventDetailResponse from(Event event) {
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getBookingOpenAt(),
                event.getBookingCloseAt(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}