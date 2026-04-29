package com.kmeans.ticketing.domain.reservation.presentation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 예매 요청 DTO
 *
 * 현재는 좌석 ID 하나를 받아 1석 예매하는 구조로 단순화한다.
 */
public record CreateReservationRequest(

        @NotNull(message = "좌석 ID는 필수입니다.")
        Long seatId
) {
}