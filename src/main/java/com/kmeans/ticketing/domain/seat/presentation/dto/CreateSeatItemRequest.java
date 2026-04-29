package com.kmeans.ticketing.domain.seat.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 좌석 1건 등록 요청 DTO
 */
public record CreateSeatItemRequest(

        @NotBlank(message = "좌석 번호는 필수입니다.")
        @Size(max = 50, message = "좌석 번호는 50자 이하여야 합니다.")
        String seatNo,

        @NotBlank(message = "좌석 구역은 필수입니다.")
        @Size(max = 50, message = "좌석 구역은 50자 이하여야 합니다.")
        String zone,

        @Min(value = 0, message = "좌석 가격은 0 이상이어야 합니다.")
        int price
) {
}