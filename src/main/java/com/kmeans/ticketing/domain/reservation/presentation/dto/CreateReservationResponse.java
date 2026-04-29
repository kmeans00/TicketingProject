package com.kmeans.ticketing.domain.reservation.presentation.dto;

import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

/**
 * 예매 요청 성공 응답 DTO
 */
public record CreateReservationResponse(
        Long reservationId,
        Long paymentId,
        Long eventId,
        Long scheduleId,
        Long seatId,
        int amount,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        LocalDateTime expiresAt
) {
    public static CreateReservationResponse of(Reservation reservation, Payment payment) {
        return new CreateReservationResponse(
                reservation.getId(),
                payment.getId(),
                reservation.getSchedule().getEvent().getId(),
                reservation.getSchedule().getId(),
                reservation.getSeat().getId(),
                reservation.getAmount(),
                reservation.getStatus(),
                payment.getStatus(),
                reservation.getExpiresAt()
        );
    }
}