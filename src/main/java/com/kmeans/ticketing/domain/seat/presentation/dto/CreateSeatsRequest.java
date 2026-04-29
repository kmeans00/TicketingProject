package com.kmeans.ticketing.domain.seat.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 좌석 일괄 등록 요청 DTO
 */
public record CreateSeatsRequest(

        @NotEmpty(message = "좌석 목록은 비어 있을 수 없습니다.")
        List<@Valid CreateSeatItemRequest> seats
) {
}