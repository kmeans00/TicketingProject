package com.kmeans.ticketing.domain.event.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 관리자 회차 등록 요청 DTO
 */
public record CreateEventScheduleRequest(

        @NotNull(message = "회차 시작 시각은 필수입니다.")
        LocalDateTime startAt,

        @NotNull(message = "회차 종료 시각은 필수입니다.")
        LocalDateTime endAt,

        @Min(value = 1, message = "전체 좌석 수는 1 이상이어야 합니다.")
        int totalSeatCount
) {
}