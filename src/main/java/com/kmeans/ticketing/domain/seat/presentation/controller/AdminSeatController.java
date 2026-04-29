package com.kmeans.ticketing.domain.seat.presentation.controller;

import com.kmeans.ticketing.domain.seat.application.service.SeatService;
import com.kmeans.ticketing.domain.seat.code.SeatSuccessCode;
import com.kmeans.ticketing.domain.seat.presentation.dto.CreateSeatsRequest;
import com.kmeans.ticketing.domain.seat.presentation.dto.CreateSeatsResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin-Seat", description = "관리자 좌석 관리 API")
@RestController
@RequestMapping("/api/v1/admin/schedules/{scheduleId}/seats")
public class AdminSeatController {

    private final SeatService seatService;

    public AdminSeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Operation(
            summary = "좌석 일괄 등록",
            description = "관리자가 특정 회차에 좌석 여러 개를 한 번에 등록합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "좌석 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회차"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 같은 좌석 번호가 존재함"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreateSeatsResponse>> createSeats(
            @PathVariable Long scheduleId,
            @Valid @RequestBody CreateSeatsRequest request
    ) {
        int createdCount = seatService.createSeats(scheduleId, request.seats());

        CreateSeatsResponse response = new CreateSeatsResponse(scheduleId, createdCount);

        return ResponseEntity
                .status(SeatSuccessCode.CREATE_SEATS_SUCCESS.getStatus())
                .body(ApiResponse.success(SeatSuccessCode.CREATE_SEATS_SUCCESS, response));
    }
}