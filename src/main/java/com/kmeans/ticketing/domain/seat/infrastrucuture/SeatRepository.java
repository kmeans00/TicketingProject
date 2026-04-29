package com.kmeans.ticketing.domain.seat.infrastructure;

import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * 같은 회차에 같은 좌석 번호가 이미 있는지 확인
     */
    boolean existsByScheduleIdAndSeatNo(Long scheduleId, String seatNo);

    /**
     * 회차별 전체 좌석 조회
     */
    List<Seat> findAllByScheduleIdOrderBySeatNoAsc(Long scheduleId);

    /**
     * 회차별 + 상태별 조회
     */
    List<Seat> findAllByScheduleIdAndStatusOrderBySeatNoAsc(Long scheduleId, SeatStatus status);

    /**
     * 회차별 + 구역별 조회
     */
    List<Seat> findAllByScheduleIdAndZoneOrderBySeatNoAsc(Long scheduleId, String zone);

    /**
     * 회차별 + 상태별 + 구역별 조회
     */
    List<Seat> findAllByScheduleIdAndStatusAndZoneOrderBySeatNoAsc(Long scheduleId, SeatStatus status, String zone);
}