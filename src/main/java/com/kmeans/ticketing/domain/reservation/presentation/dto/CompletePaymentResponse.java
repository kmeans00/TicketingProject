package com.kmeans.ticketing.domain.reservation.presentation.dto;

import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;

/**
 * 결제 Mock 성공 처리 응답 DTO
 */
public record CompletePaymentResponse(
        Long reservationId,
        Long paymentId,
        ReservationStatus reservationStatus,
        PaymentStatus paymentStatus,
        SeatStatus seatStatus
) {
    public static CompletePaymentResponse of(Reservation reservation, Payment payment) {
        return new CompletePaymentResponse(
                reservation.getId(),
                payment.getId(),
                reservation.getStatus(),
                payment.getStatus(),
                reservation.getSeat().getStatus()
        );
    }
}