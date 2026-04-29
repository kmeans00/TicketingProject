package com.kmeans.ticketing.domain.payment.domain;

import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 결제 엔티티
 *
 * 예매 1건에 결제 1건이 연결되는 구조로 단순화한다.
 */
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 예매에 대한 결제인지
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    /**
     * 결제 금액
     */
    @Column(nullable = false)
    private int amount;

    /**
     * 결제 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    /**
     * 결제 성공 시각
     */
    private LocalDateTime paidAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(Reservation reservation,
                    int amount,
                    PaymentStatus status) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = status;
    }

    public static Payment create(Reservation reservation, int amount) {
        return Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .status(PaymentStatus.READY)
                .build();
    }

    public boolean isReady() {
        return this.status == PaymentStatus.READY;
    }

    public void success() {
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
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