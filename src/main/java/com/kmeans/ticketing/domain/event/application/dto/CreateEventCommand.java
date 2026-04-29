package com.kmeans.ticketing.domain.event.application.dto;

import java.time.LocalDateTime;

/**
 * 행사 등록 유스케이스 실행용 객체
 * Controller에서 받은 요청값을 Service에 전달할 때 사용한다.
 */
public record CreateEventCommand(
        String title,
        String description,
        String location,
        LocalDateTime bookingOpenAt,
        LocalDateTime bookingCloseAt
) {
}