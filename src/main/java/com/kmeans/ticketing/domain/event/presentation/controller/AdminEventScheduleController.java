package com.kmeans.ticketing.domain.event.presentation.controller;

import com.kmeans.ticketing.domain.event.application.service.EventScheduleService;
import com.kmeans.ticketing.domain.event.code.EventScheduleSuccessCode;
import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.event.presentation.dto.CreateEventScheduleRequest;
import com.kmeans.ticketing.domain.event.presentation.dto.CreateEventScheduleResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin-Event", description = "관리자 행사 관리 API")
@RestController
@RequestMapping("/api/v1/admin/events/{eventId}/schedules")
public class AdminEventScheduleController {

    private final EventScheduleService eventScheduleService;

    public AdminEventScheduleController(EventScheduleService eventScheduleService) {
        this.eventScheduleService = eventScheduleService;
    }

    @Operation(
            summary = "회차 등록",
            description = "관리자가 특정 행사에 새로운 회차를 등록합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회차 등록 성공"
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
                    description = "존재하지 않는 행사"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 같은 시작 시각의 회차가 존재함"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreateEventScheduleResponse>> createEventSchedule(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateEventScheduleRequest request
    ) {
        EventSchedule schedule = eventScheduleService.createEventSchedule(
                eventId,
                request.startAt(),
                request.endAt(),
                request.totalSeatCount()
        );

        CreateEventScheduleResponse response = CreateEventScheduleResponse.from(schedule);

        return ResponseEntity
                .status(EventScheduleSuccessCode.CREATE_EVENT_SCHEDULE_SUCCESS.getStatus())
                .body(ApiResponse.success(EventScheduleSuccessCode.CREATE_EVENT_SCHEDULE_SUCCESS, response));
    }
}