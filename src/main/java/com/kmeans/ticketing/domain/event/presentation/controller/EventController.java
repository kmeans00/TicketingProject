package com.kmeans.ticketing.domain.event.presentation.controller;

import com.kmeans.ticketing.domain.event.application.service.EventService;
import com.kmeans.ticketing.domain.event.code.EventSuccessCode;
import com.kmeans.ticketing.domain.event.domain.EventStatus;
import com.kmeans.ticketing.domain.event.presentation.dto.EventDetailResponse;
import com.kmeans.ticketing.domain.event.presentation.dto.EventSummaryResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event", description = "행사 조회 API")
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "행사 목록 조회",
            description = "행사 목록을 조회합니다. 필요하면 status로 상태별 필터링할 수 있습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "행사 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태값 요청"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventSummaryResponse>>> getEvents(
            @RequestParam(required = false) EventStatus status
    ) {
        List<EventSummaryResponse> response = eventService.getEventSummaries(status);

        return ResponseEntity
                .status(EventSuccessCode.GET_EVENT_LIST_SUCCESS.getStatus())
                .body(ApiResponse.success(EventSuccessCode.GET_EVENT_LIST_SUCCESS, response));
    }

    @Operation(
            summary = "행사 상세 조회",
            description = "행사 ID로 특정 행사 1건의 상세 정보를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "행사 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 행사"
            )
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDetailResponse>> getEvent(
            @PathVariable Long eventId
    ) {
        EventDetailResponse response = eventService.getEventDetail(eventId);

        return ResponseEntity
                .status(EventSuccessCode.GET_EVENT_DETAIL_SUCCESS.getStatus())
                .body(ApiResponse.success(EventSuccessCode.GET_EVENT_DETAIL_SUCCESS, response));
    }
}