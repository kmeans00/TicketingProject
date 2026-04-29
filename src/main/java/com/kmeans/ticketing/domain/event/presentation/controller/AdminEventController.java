package com.kmeans.ticketing.domain.event.presentation.controller;

import com.kmeans.ticketing.domain.event.application.dto.CreateEventCommand;
import com.kmeans.ticketing.domain.event.application.dto.CreateEventResult;
import com.kmeans.ticketing.domain.event.application.service.EventService;
import com.kmeans.ticketing.domain.event.code.EventSuccessCode;
import com.kmeans.ticketing.domain.event.presentation.dto.CreateEventRequest;
import com.kmeans.ticketing.domain.event.presentation.dto.CreateEventResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin-Event", description = "관리자 행사 관리 API")
@RestController
@RequestMapping("/api/v1/admin/events")
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "행사 등록",
            description = "관리자가 새로운 행사를 등록합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "행사 등록 성공"
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
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreateEventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        CreateEventResult result = eventService.createEvent(
                new CreateEventCommand(
                        request.title(),
                        request.description(),
                        request.location(),
                        request.bookingOpenAt(),
                        request.bookingCloseAt()
                )
        );

        CreateEventResponse response = CreateEventResponse.from(result);

        return ResponseEntity
                .status(EventSuccessCode.CREATE_EVENT_SUCCESS.getStatus())
                .body(ApiResponse.success(EventSuccessCode.CREATE_EVENT_SUCCESS, response));
    }
}