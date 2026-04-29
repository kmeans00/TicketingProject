package com.kmeans.ticketing.domain.settlement.presentation.dto;

import com.kmeans.ticketing.domain.settlement.domain.SettlementDaily;

import java.time.LocalDate;

public record RunSettlementResponse(
        LocalDate settlementDate,
        long paymentCount,
        long totalAmount
) {
    public static RunSettlementResponse from(SettlementDaily settlementDaily) {
        return new RunSettlementResponse(
                settlementDaily.getSettlementDate(),
                settlementDaily.getPaymentCount(),
                settlementDaily.getTotalAmount()
        );
    }
}