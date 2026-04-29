package com.kmeans.ticketing.domain.event.presentation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 관리자가 행사 등록 시 보내는 요청 DTO
 */
public record CreateEventRequest(

        @NotBlank(message = "행사명은 필수입니다.")
        @Size(max = 255, message = "행사명은 255자 이하여야 합니다.")
        String title,

        @Size(max = 5000, message = "행사 설명은 너무 길 수 없습니다.")
        String description,

        @NotBlank(message = "행사 장소는 필수입니다.")
        @Size(max = 255, message = "행사 장소는 255자 이하여야 합니다.")
        String location,

        @NotNull(message = "예매 오픈 시각은 필수입니다.")
        LocalDateTime bookingOpenAt,

        @NotNull(message = "예매 마감 시각은 필수입니다.")
        LocalDateTime bookingCloseAt
) {
}