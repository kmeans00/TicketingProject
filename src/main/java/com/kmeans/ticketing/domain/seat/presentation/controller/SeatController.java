package com.kmeans.ticketing.domain.seat.presentation.controller;

import com.kmeans.ticketing.domain.seat.application.service.SeatService;
import com.kmeans.ticketing.domain.seat.code.SeatSuccessCode;
import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;
import com.kmeans.ticketing.domain.seat.presentation.dto.SeatSummaryResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Seat", description = "좌석 조회 API")
@RestController
@RequestMapping("/api/v1/schedules/{scheduleId}/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @Operation(
            summary = "회차별 좌석 조회",
            description = "특정 회차의 좌석 목록을 조회합니다. 상태(status), 구역(zone)으로 필터링할 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "좌석 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회차"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatSummaryResponse>>> getSeats(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) SeatStatus status,
            @RequestParam(required = false) String zone
    ) {
        List<Seat> seats = seatService.getSeats(scheduleId, status, zone);

        List<SeatSummaryResponse> response = seats.stream()
                .map(SeatSummaryResponse::from)
                .toList();

        return ResponseEntity
                .status(SeatSuccessCode.GET_SEAT_LIST_SUCCESS.getStatus())
                .body(ApiResponse.success(SeatSuccessCode.GET_SEAT_LIST_SUCCESS, response));
    }
}