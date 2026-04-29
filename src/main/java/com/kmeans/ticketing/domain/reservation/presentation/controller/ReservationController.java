package com.kmeans.ticketing.domain.reservation.presentation.controller;

import com.kmeans.ticketing.domain.reservation.application.service.ReservationService;
import com.kmeans.ticketing.domain.reservation.code.ReservationSuccessCode;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.presentation.dto.CancelReservationResponse;
import com.kmeans.ticketing.domain.reservation.presentation.dto.CompletePaymentResponse;
import com.kmeans.ticketing.domain.reservation.presentation.dto.CreateReservationRequest;
import com.kmeans.ticketing.domain.reservation.presentation.dto.CreateReservationResponse;
import com.kmeans.ticketing.domain.reservation.presentation.dto.ReservationDetailResponse;
import com.kmeans.ticketing.domain.reservation.presentation.dto.ReservationSummaryResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import com.kmeans.ticketing.global.security.JwtUserInfo;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * 내 예매 목록 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ReservationSummaryResponse>>> getMyReservations(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        List<ReservationSummaryResponse> response =
                reservationService.getMyReservations(userInfo.userId());

        return ResponseEntity
                .status(ReservationSuccessCode.GET_MY_RESERVATION_LIST_SUCCESS.getStatus())
                .body(ApiResponse.success(ReservationSuccessCode.GET_MY_RESERVATION_LIST_SUCCESS, response));
    }

    /**
     * 내 예매 상세 조회 API
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDetailResponse>> getMyReservation(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long reservationId
    ) {
        Reservation reservation = reservationService.getMyReservation(userInfo.userId(), reservationId);

        ReservationDetailResponse response = ReservationDetailResponse.from(reservation);

        return ResponseEntity
                .status(ReservationSuccessCode.GET_MY_RESERVATION_DETAIL_SUCCESS.getStatus())
                .body(ApiResponse.success(ReservationSuccessCode.GET_MY_RESERVATION_DETAIL_SUCCESS, response));
    }

    /**
     * 예매 요청 API
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationService.CreateReservationHolder holder =
                reservationService.createReservation(userInfo.userId(), request.seatId());

        CreateReservationResponse response =
                CreateReservationResponse.of(holder.reservation(), holder.payment());

        return ResponseEntity
                .status(ReservationSuccessCode.CREATE_RESERVATION_SUCCESS.getStatus())
                .body(ApiResponse.success(ReservationSuccessCode.CREATE_RESERVATION_SUCCESS, response));
    }

    /**
     * 결제 Mock 성공 처리 API
     */
    @PostMapping("/{reservationId}/payments/success")
    public ResponseEntity<ApiResponse<CompletePaymentResponse>> completePayment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long reservationId
    ) {
        ReservationService.CreateReservationHolder holder =
                reservationService.completePayment(userInfo.userId(), reservationId);

        CompletePaymentResponse response =
                CompletePaymentResponse.of(holder.reservation(), holder.payment());

        return ResponseEntity
                .status(ReservationSuccessCode.COMPLETE_PAYMENT_SUCCESS.getStatus())
                .body(ApiResponse.success(ReservationSuccessCode.COMPLETE_PAYMENT_SUCCESS, response));
    }

    /**
     * 예매 취소 API
     */
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<CancelReservationResponse>> cancelReservation(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @PathVariable Long reservationId
    ) {
        ReservationService.CreateReservationHolder holder =
                reservationService.cancelReservation(userInfo.userId(), reservationId);

        CancelReservationResponse response =
                CancelReservationResponse.of(holder.reservation(), holder.payment());

        return ResponseEntity
                .status(ReservationSuccessCode.CANCEL_RESERVATION_SUCCESS.getStatus())
                .body(ApiResponse.success(ReservationSuccessCode.CANCEL_RESERVATION_SUCCESS, response));
    }
}