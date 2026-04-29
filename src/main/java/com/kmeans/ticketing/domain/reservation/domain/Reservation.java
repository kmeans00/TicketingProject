package com.kmeans.ticketing.domain.reservation.domain;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 예매 엔티티
 *
 * 사용자(User)가 특정 회차(EventSchedule)의 특정 좌석(Seat)을 예매한 기록
 */
@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 예매한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 예매한 회차
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private EventSchedule schedule;

    /**
     * 예매한 좌석
     *
     * 좌석은 시간이 지나면서 여러 예매 이력에 등장할 수 있으므로
     * seat_id에 unique 제약을 두지 않는다.
     *
     * 현재 활성 예매 중복 방지는 Seat.status로 제어한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    /**
     * 이 예매에 연결된 결제 정보
     *
     * Payment가 reservation_id 외래키를 소유하고 있으므로 mappedBy로 역방향만 둔다.
     * 테이블 구조를 바꾸는 게 아니라 조회 편의를 위한 연관관계다.
     */
    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Payment payment;

    /**
     * 결제 금액
     */
    @Column(nullable = false)
    private int amount;

    /**
     * 예매 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    /**
     * 결제 대기 만료 시각
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Reservation(User user,
                        EventSchedule schedule,
                        Seat seat,
                        int amount,
                        ReservationStatus status,
                        LocalDateTime expiresAt) {
        this.user = user;
        this.schedule = schedule;
        this.seat = seat;
        this.amount = amount;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public static Reservation create(User user,
                                     EventSchedule schedule,
                                     Seat seat,
                                     int amount,
                                     LocalDateTime expiresAt) {
        return Reservation.builder()
                .user(user)
                .schedule(schedule)
                .seat(seat)
                .amount(amount)
                .status(ReservationStatus.PENDING_PAYMENT)
                .expiresAt(expiresAt)
                .build();
    }

    public boolean isPendingPayment() {
        return this.status == ReservationStatus.PENDING_PAYMENT;
    }

    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isCanceled() {
        return this.status == ReservationStatus.CANCELED;
    }

    public boolean isExpiredStatus() {
        return this.status == ReservationStatus.EXPIRED;
    }

    public boolean isCancelable() {
        return this.status == ReservationStatus.PENDING_PAYMENT
                || this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiresAt.isBefore(now);
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
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