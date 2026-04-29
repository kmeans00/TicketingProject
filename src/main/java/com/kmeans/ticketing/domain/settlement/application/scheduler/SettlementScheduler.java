package com.kmeans.ticketing.domain.settlement.application.scheduler;

import com.kmeans.ticketing.domain.settlement.application.service.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class SettlementScheduler {

    private final SettlementService settlementService;

    public SettlementScheduler(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /**
     * 매일 새벽 1시에 전날 정산 집계
     */
    @Scheduled(cron = "${settlement.daily.cron:0 0 1 * * *}")
    public void settleYesterday() {
        LocalDate targetDate = LocalDate.now().minusDays(1);

        settlementService.settleDaily(targetDate);

        log.info("[SETTLEMENT-BATCH] daily settlement completed. targetDate={}", targetDate);
    }
}