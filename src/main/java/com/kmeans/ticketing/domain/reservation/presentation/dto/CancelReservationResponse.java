package com.kmeans.ticketing.domain.reservation.presentation.dto;

import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;

/**
 * 예매 취소 응답 DTO
 */
public record CancelReservationResponse(
        Long reservationId,
        Long paymentId,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        SeatStatus seatStatus,
        int availableSeatCount
) {
    public static CancelReservationResponse of(Reservation reservation, Payment payment) {
        return new CancelReservationResponse(
                reservation.getId(),
                payment.getId(),
                reservation.getStatus(),
                payment.getStatus(),
                reservation.getSeat().getStatus(),
                reservation.getSchedule().getAvailableSeatCount()
        );
    }
}