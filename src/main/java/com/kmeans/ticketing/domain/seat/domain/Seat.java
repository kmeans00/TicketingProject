package com.kmeans.ticketing.domain.seat.domain;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 좌석 엔티티
 *
 * 하나의 회차(EventSchedule) 아래에 여러 좌석이 존재한다.
 */
@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_schedule_id_seat_no",
                        columnNames = {"schedule_id", "seat_no"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 회차에 속한 좌석인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private EventSchedule schedule;

    /**
     * 좌석 번호
     * 예: A-1, A-2, B-10
     */
    @Column(name = "seat_no", nullable = false, length = 50)
    private String seatNo;

    /**
     * 좌석 구역
     * 예: VIP, R, S
     *
     * 지금은 문자열로 두고, 필요하면 나중에 enum으로 분리 가능
     */
    @Column(nullable = false, length = 50)
    private String zone;

    /**
     * 좌석 가격
     */
    @Column(nullable = false)
    private int price;

    /**
     * 좌석 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    /**
     * 낙관적 락 버전
     *
     * 나중에 동일 좌석 동시 예매 충돌 제어할 때 사용 가능
     */
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Seat(EventSchedule schedule,
                 String seatNo,
                 String zone,
                 int price,
                 SeatStatus status) {
        this.schedule = schedule;
        this.seatNo = seatNo;
        this.zone = zone;
        this.price = price;
        this.status = status;
    }

    /**
     * 좌석 생성 시 기본 상태는 AVAILABLE
     */
    public static Seat create(EventSchedule schedule,
                              String seatNo,
                              String zone,
                              int price) {
        return Seat.builder()
                .schedule(schedule)
                .seatNo(seatNo)
                .zone(zone)
                .price(price)
                .status(SeatStatus.AVAILABLE)
                .build();
    }

    public boolean isAvailable() {
        return this.status == SeatStatus.AVAILABLE;
    }

    public void reserve() {
        this.status = SeatStatus.RESERVED;
    }

    public void sell() {
        this.status = SeatStatus.SOLD;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void block() {
        this.status = SeatStatus.BLOCKED;
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