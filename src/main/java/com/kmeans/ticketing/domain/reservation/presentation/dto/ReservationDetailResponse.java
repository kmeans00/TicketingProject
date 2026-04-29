package com.kmeans.ticketing.domain.reservation.presentation.dto;

import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;

import java.time.LocalDateTime;

/**
 * 내 예매 상세 조회 응답 DTO
 */
public record ReservationDetailResponse(
        Long reservationId,
        Long eventId,
        String eventTitle,
        String eventLocation,
        Long scheduleId,
        LocalDateTime scheduleStartAt,
        LocalDateTime scheduleEndAt,
        Long seatId,
        String seatNo,
        String zone,
        int amount,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        SeatStatus seatStatus,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReservationDetailResponse from(Reservation reservation) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getSchedule().getEvent().getId(),
                reservation.getSchedule().getEvent().getTitle(),
                reservation.getSchedule().getEvent().getLocation(),
                reservation.getSchedule().getId(),
                reservation.getSchedule().getStartAt(),
                reservation.getSchedule().getEndAt(),
                reservation.getSeat().getId(),
                reservation.getSeat().getSeatNo(),
                reservation.getSeat().getZone(),
                reservation.getAmount(),
                reservation.getStatus(),
                reservation.getPayment() != null ? reservation.getPayment().getStatus() : null,
                reservation.getSeat().getStatus(),
                reservation.getExpiresAt(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}