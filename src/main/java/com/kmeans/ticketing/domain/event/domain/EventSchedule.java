package com.kmeans.ticketing.domain.event.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 행사 회차 엔티티
 *
 * 예:
 * - 5월 1일 14시 공연
 * - 5월 1일 19시 공연
 *
 * 예매는 행사(Event) 자체가 아니라 특정 회차(EventSchedule) 기준으로 일어난다.
 */
@Entity
@Table(
        name = "event_schedules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_schedule_event_id_start_at",
                        columnNames = {"event_id", "start_at"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 행사에 속한 회차인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * 회차 시작 시각
     */
    @Column(nullable = false)
    private LocalDateTime startAt;

    /**
     * 회차 종료 시각
     */
    @Column(nullable = false)
    private LocalDateTime endAt;

    /**
     * 전체 좌석 수
     */
    @Column(nullable = false)
    private int totalSeatCount;

    /**
     * 현재 예매 가능한 좌석 수
     *
     * 처음 생성 시에는 totalSeatCount와 동일하게 시작한다.
     * 이후 예매/취소/만료 때 증감된다.
     */
    @Column(nullable = false)
    private int availableSeatCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private EventSchedule(Event event,
                          LocalDateTime startAt,
                          LocalDateTime endAt,
                          int totalSeatCount,
                          int availableSeatCount) {
        this.event = event;
        this.startAt = startAt;
        this.endAt = endAt;
        this.totalSeatCount = totalSeatCount;
        this.availableSeatCount = availableSeatCount;
    }

    /**
     * 회차 생성
     *
     * 처음에는 남은 좌석 수를 전체 좌석 수와 같게 둔다.
     */
    public static EventSchedule create(Event event,
                                       LocalDateTime startAt,
                                       LocalDateTime endAt,
                                       int totalSeatCount) {
        return EventSchedule.builder()
                .event(event)
                .startAt(startAt)
                .endAt(endAt)
                .totalSeatCount(totalSeatCount)
                .availableSeatCount(totalSeatCount)
                .build();
    }

    /**
     * 회차 시작 시각이 이미 지났는지 확인
     */
    public boolean isStarted(LocalDateTime now) {
        return !this.startAt.isAfter(now);
    }

    /**
     * 남은 좌석이 있는지 확인
     */
    public boolean hasAvailableSeat() {
        return this.availableSeatCount > 0;
    }

    /**
     * 예매 성공 시 남은 좌석 수 감소
     */
    public void decreaseAvailableSeatCount() {
        if (this.availableSeatCount <= 0) {
            throw new IllegalStateException("남은 좌석이 없습니다.");
        }
        this.availableSeatCount--;
    }

    /**
     * 예매 취소/만료 시 남은 좌석 수 증가
     */
    public void increaseAvailableSeatCount() {
        if (this.availableSeatCount >= this.totalSeatCount) {
            throw new IllegalStateException("가용 좌석 수가 전체 좌석 수를 초과할 수 없습니다.");
        }
        this.availableSeatCount++;
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