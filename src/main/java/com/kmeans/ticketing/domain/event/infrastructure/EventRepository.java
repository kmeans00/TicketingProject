package com.kmeans.ticketing.domain.event.infrastructure;

import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 전체 행사 목록을 최신 생성순으로 조회
     */
    List<Event> findAllByOrderByCreatedAtDesc();

    /**
     * 특정 상태의 행사 목록을 최신 생성순으로 조회
     */
    List<Event> findAllByStatusOrderByCreatedAtDesc(EventStatus status);
}