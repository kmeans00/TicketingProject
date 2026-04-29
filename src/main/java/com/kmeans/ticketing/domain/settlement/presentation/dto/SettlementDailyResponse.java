package com.kmeans.ticketing.domain.settlement.presentation.dto;

import com.kmeans.ticketing.domain.settlement.domain.SettlementDaily;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SettlementDailyResponse(
        Long id,
        LocalDate settlementDate,
        long paymentCount,
        long totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SettlementDailyResponse from(SettlementDaily settlementDaily) {
        return new SettlementDailyResponse(
                settlementDaily.getId(),
                settlementDaily.getSettlementDate(),
                settlementDaily.getPaymentCount(),
                settlementDaily.getTotalAmount(),
                settlementDaily.getCreatedAt(),
                settlementDaily.getUpdatedAt()
        );
    }
}