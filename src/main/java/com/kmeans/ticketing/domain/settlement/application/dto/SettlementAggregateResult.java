package com.kmeans.ticketing.domain.settlement.application.dto;

public record SettlementAggregateResult(
        long paymentCount,
        long totalAmount
) {
}