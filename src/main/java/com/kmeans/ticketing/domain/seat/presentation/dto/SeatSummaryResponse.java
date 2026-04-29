package com.kmeans.ticketing.domain.seat.presentation.dto;

import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;

/**
 * 좌석 목록 조회 시 각 좌석 1건을 표현하는 응답 DTO
 */
public record SeatSummaryResponse(
        Long seatId,
        String seatNo,
        String zone,
        int price,
        SeatStatus status
) {
    public static SeatSummaryResponse from(Seat seat) {
        return new SeatSummaryResponse(
                seat.getId(),
                seat.getSeatNo(),
                seat.getZone(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}