package com.kmeans.ticketing.domain.event.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 행사명
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 행사 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 행사 장소
     */
    @Column(nullable = false, length = 255)
    private String location;

    /**
     * 예매 오픈 시각
     */
    @Column(nullable = false)
    private LocalDateTime bookingOpenAt;

    /**
     * 예매 마감 시각
     */
    @Column(nullable = false)
    private LocalDateTime bookingCloseAt;

    /**
     * 행사 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Event(String title,
                  String description,
                  String location,
                  LocalDateTime bookingOpenAt,
                  LocalDateTime bookingCloseAt,
                  EventStatus status) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.bookingOpenAt = bookingOpenAt;
        this.bookingCloseAt = bookingCloseAt;
        this.status = status;
    }

    /**
     * 행사 생성용 정적 팩토리 메서드
     * 관리자 행사 등록 시 이 메서드를 통해 Event를 만든다.
     */
    public static Event create(String title,
                               String description,
                               String location,
                               LocalDateTime bookingOpenAt,
                               LocalDateTime bookingCloseAt) {
        return Event.builder()
                .title(title)
                .description(description)
                .location(location)
                .bookingOpenAt(bookingOpenAt)
                .bookingCloseAt(bookingCloseAt)
                .status(EventStatus.READY)
                .build();
    }

    /**
     * 현재 예매 가능한 행사인지 확인할 때 사용할 수 있는 메서드
     */
    public boolean isOpen() {
        return this.status == EventStatus.OPEN;
    }

    /**
     * 상태 변경 메서드
     * 나중에 관리자 기능에서 행사 상태 변경할 때 사용 가능
     */
    public void changeStatus(EventStatus status) {
        this.status = status;
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