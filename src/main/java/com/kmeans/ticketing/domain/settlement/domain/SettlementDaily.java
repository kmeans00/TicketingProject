package com.kmeans.ticketing.domain.settlement.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "settlement_daily",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_settlement_daily_date", columnNames = "settlement_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 정산 기준 일자
     */
    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    /**
     * 결제 성공 건수
     */
    @Column(nullable = false)
    private long paymentCount;

    /**
     * 총 정산 금액
     */
    @Column(nullable = false)
    private long totalAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private SettlementDaily(LocalDate settlementDate,
                            long paymentCount,
                            long totalAmount) {
        this.settlementDate = settlementDate;
        this.paymentCount = paymentCount;
        this.totalAmount = totalAmount;
    }

    public static SettlementDaily create(LocalDate settlementDate,
                                         long paymentCount,
                                         long totalAmount) {
        return SettlementDaily.builder()
                .settlementDate(settlementDate)
                .paymentCount(paymentCount)
                .totalAmount(totalAmount)
                .build();
    }

    public void updateSummary(long paymentCount, long totalAmount) {
        this.paymentCount = paymentCount;
        this.totalAmount = totalAmount;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}