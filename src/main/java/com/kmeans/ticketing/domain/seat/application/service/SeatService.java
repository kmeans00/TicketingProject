package com.kmeans.ticketing.domain.seat.application.service;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.event.infrastructure.EventScheduleRepository;
import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.seat.domain.SeatStatus;
import com.kmeans.ticketing.domain.seat.exception.SeatErrorCode;
import com.kmeans.ticketing.domain.seat.infrastructure.SeatRepository;
import com.kmeans.ticketing.domain.seat.presentation.dto.CreateSeatItemRequest;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SeatService {

    private final EventScheduleRepository eventScheduleRepository;
    private final SeatRepository seatRepository;

    public SeatService(EventScheduleRepository eventScheduleRepository,
                       SeatRepository seatRepository) {
        this.eventScheduleRepository = eventScheduleRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * 관리자 좌석 일괄 등록
     *
     * 처리 흐름:
     * 1. 부모 회차 존재 여부 확인
     * 2. 요청 내부 중복 좌석 번호 검사
     * 3. DB에 이미 있는 좌석 번호 중복 검사
     * 4. 좌석 생성 후 저장
     */
    @Transactional
    public int createSeats(Long scheduleId, List<CreateSeatItemRequest> requests) {
        EventSchedule schedule = eventScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new TicketingException(SeatErrorCode.EVENT_SCHEDULE_NOT_FOUND));

        validateDuplicateSeatNoInRequest(requests);

        for (CreateSeatItemRequest request : requests) {
            if (seatRepository.existsByScheduleIdAndSeatNo(scheduleId, request.seatNo())) {
                throw new TicketingException(SeatErrorCode.SEAT_ALREADY_EXISTS);
            }
        }

        List<Seat> seats = requests.stream()
                .map(request -> Seat.create(
                        schedule,
                        request.seatNo(),
                        request.zone(),
                        request.price()
                ))
                .toList();

        seatRepository.saveAll(seats);

        return seats.size();
    }

    /**
     * 회차별 좌석 목록 조회
     *
     * 필터 조합:
     * - status 없음, zone 없음  -> 전체 조회
     * - status 있음, zone 없음  -> 상태별 조회
     * - status 없음, zone 있음  -> 구역별 조회
     * - status 있음, zone 있음  -> 상태 + 구역 조회
     */
    @Transactional(readOnly = true)
    public List<Seat> getSeats(Long scheduleId, SeatStatus status, String zone) {
        if (!eventScheduleRepository.existsById(scheduleId)) {
            throw new TicketingException(SeatErrorCode.EVENT_SCHEDULE_NOT_FOUND);
        }

        boolean hasStatus = status != null;
        boolean hasZone = zone != null && !zone.isBlank();

        if (!hasStatus && !hasZone) {
            return seatRepository.findAllByScheduleIdOrderBySeatNoAsc(scheduleId);
        }

        if (hasStatus && !hasZone) {
            return seatRepository.findAllByScheduleIdAndStatusOrderBySeatNoAsc(scheduleId, status);
        }

        if (!hasStatus) {
            return seatRepository.findAllByScheduleIdAndZoneOrderBySeatNoAsc(scheduleId, zone);
        }

        return seatRepository.findAllByScheduleIdAndStatusAndZoneOrderBySeatNoAsc(scheduleId, status, zone);
    }

    /**
     * 같은 요청 안에서 좌석 번호가 중복되면 안 된다.
     *
     * 예:
     * A-1, A-2, A-1  -> 중복
     */
    private void validateDuplicateSeatNoInRequest(List<CreateSeatItemRequest> requests) {
        Set<String> seatNos = new HashSet<>();

        for (CreateSeatItemRequest request : requests) {
            if (!seatNos.add(request.seatNo())) {
                throw new TicketingException(SeatErrorCode.SEAT_ALREADY_EXISTS);
            }
        }
    }
}