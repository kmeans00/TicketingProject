package com.kmeans.ticketing.domain.event.presentation.controller;

import com.kmeans.ticketing.domain.event.application.service.EventScheduleService;
import com.kmeans.ticketing.domain.event.code.EventScheduleSuccessCode;
import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.event.presentation.dto.EventScheduleSummaryResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "행사 조회 API")
@RestController
@RequestMapping("/api/v1/events/{eventId}/schedules")
public class EventScheduleController {

    private final EventScheduleService eventScheduleService;

    public EventScheduleController(EventScheduleService eventScheduleService) {
        this.eventScheduleService = eventScheduleService;
    }

    @Operation(
            summary = "행사별 회차 목록 조회",
            description = "특정 행사에 속한 회차 목록을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회차 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 행사"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventScheduleSummaryResponse>>> getEventSchedules(
            @PathVariable Long eventId
    ) {
        List<EventSchedule> schedules = eventScheduleService.getEventSchedules(eventId);

        List<EventScheduleSummaryResponse> response = schedules.stream()
                .map(EventScheduleSummaryResponse::from)
                .toList();

        return ResponseEntity
                .status(EventScheduleSuccessCode.GET_EVENT_SCHEDULE_LIST_SUCCESS.getStatus())
                .body(ApiResponse.success(EventScheduleSuccessCode.GET_EVENT_SCHEDULE_LIST_SUCCESS, response));
    }
}