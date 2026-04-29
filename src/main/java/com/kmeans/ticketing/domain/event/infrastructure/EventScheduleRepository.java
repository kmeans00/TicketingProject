package com.kmeans.ticketing.domain.event.infrastructure;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    /**
     * 특정 행사에 속한 회차 목록을 시작 시각 오름차순으로 조회
     */
    List<EventSchedule> findAllByEventIdOrderByStartAtAsc(Long eventId);

    /**
     * 같은 이벤트에 같은 시작 시각의 회차가 이미 존재하는지 확인
     */
    boolean existsByEventIdAndStartAt(Long eventId, LocalDateTime startAt);

    /**
     * 가용 좌석 수를 원자적으로 1 감소시킨다.
     *
     * 반환값:
     * - 1 : 감소 성공
     * - 0 : 감소 실패 (이미 0이거나 존재하지 않음)
     */
    @Modifying(flushAutomatically = true, clearAutomatically = false)
    @Query("""
            update EventSchedule es
            set es.availableSeatCount = es.availableSeatCount - 1
            where es.id = :scheduleId
              and es.availableSeatCount > 0
            """)
    int decreaseAvailableSeatCount(@Param("scheduleId") Long scheduleId);
}