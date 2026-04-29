package com.kmeans.ticketing.domain.settlement.presentation.controller;

import com.kmeans.ticketing.domain.settlement.application.service.SettlementService;
import com.kmeans.ticketing.domain.settlement.code.SettlementSuccessCode;
import com.kmeans.ticketing.domain.settlement.domain.SettlementDaily;
import com.kmeans.ticketing.domain.settlement.presentation.dto.RunSettlementResponse;
import com.kmeans.ticketing.domain.settlement.presentation.dto.SettlementDailyResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/settlements/daily")
public class AdminSettlementController {

    private final SettlementService settlementService;

    public AdminSettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /**
     * 수동 정산 실행 API
     *
     * 테스트 편의를 위해 둔다.
     * date 미입력 시 전날 기준으로 정산
     */
    @PostMapping("/run")
    public ResponseEntity<ApiResponse<RunSettlementResponse>> runSettlement(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now().minusDays(1);

        SettlementDaily settlementDaily = settlementService.settleDaily(targetDate);

        RunSettlementResponse response = RunSettlementResponse.from(settlementDaily);

        return ResponseEntity
                .status(SettlementSuccessCode.RUN_DAILY_SETTLEMENT_SUCCESS.getStatus())
                .body(ApiResponse.success(SettlementSuccessCode.RUN_DAILY_SETTLEMENT_SUCCESS, response));
    }

    /**
     * 일별 정산 1건 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<SettlementDailyResponse>> getSettlementDaily(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        SettlementDaily settlementDaily = settlementService.getSettlementDaily(date);

        SettlementDailyResponse response = SettlementDailyResponse.from(settlementDaily);

        return ResponseEntity
                .status(SettlementSuccessCode.GET_DAILY_SETTLEMENT_SUCCESS.getStatus())
                .body(ApiResponse.success(SettlementSuccessCode.GET_DAILY_SETTLEMENT_SUCCESS, response));
    }

    /**
     * 기간별 일별 정산 목록 조회 API
     */
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<SettlementDailyResponse>>> getSettlementDailyList(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        List<SettlementDailyResponse> response = settlementService.getSettlementDailyList(from, to).stream()
                .map(SettlementDailyResponse::from)
                .toList();

        return ResponseEntity
                .status(SettlementSuccessCode.GET_DAILY_SETTLEMENT_LIST_SUCCESS.getStatus())
                .body(ApiResponse.success(SettlementSuccessCode.GET_DAILY_SETTLEMENT_LIST_SUCCESS, response));
    }
}