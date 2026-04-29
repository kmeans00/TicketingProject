package com.kmeans.ticketing.domain.reservation.presentation.dto;

import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

/**
 * 내 예매 목록 조회 응답 DTO
 */
public record ReservationSummaryResponse(
        Long reservationId,
        Long eventId,
        String eventTitle,
        Long scheduleId,
        LocalDateTime scheduleStartAt,
        Long seatId,
        String seatNo,
        String zone,
        int amount,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt
) {
    public static ReservationSummaryResponse from(Reservation reservation) {
        return new ReservationSummaryResponse(
                reservation.getId(),
                reservation.getSchedule().getEvent().getId(),
                reservation.getSchedule().getEvent().getTitle(),
                reservation.getSchedule().getId(),
                reservation.getSchedule().getStartAt(),
                reservation.getSeat().getId(),
                reservation.getSeat().getSeatNo(),
                reservation.getSeat().getZone(),
                reservation.getAmount(),
                reservation.getStatus(),
                reservation.getPayment() != null ? reservation.getPayment().getStatus() : null,
                reservation.getCreatedAt()
        );
    }
}