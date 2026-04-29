package com.kmeans.ticketing.domain.event.application.service;

import com.kmeans.ticketing.domain.common.exception.CommonErrorCode;
import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.event.exception.EventErrorCode;
import com.kmeans.ticketing.domain.event.exception.EventScheduleErrorCode;
import com.kmeans.ticketing.domain.event.infrastructure.EventRepository;
import com.kmeans.ticketing.domain.event.infrastructure.EventScheduleRepository;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventScheduleService {

    private final EventRepository eventRepository;
    private final EventScheduleRepository eventScheduleRepository;

    public EventScheduleService(EventRepository eventRepository,
                                EventScheduleRepository eventScheduleRepository) {
        this.eventRepository = eventRepository;
        this.eventScheduleRepository = eventScheduleRepository;
    }

    /**
     * 관리자 회차 등록
     *
     * 처리 흐름:
     * 1. 부모 행사 존재 여부 확인
     * 2. 비즈니스 규칙 검증
     * 3. 같은 이벤트에 같은 시작 시각 회차가 있는지 확인
     * 4. EventSchedule 생성
     * 5. 저장 후 반환
     */
    @Transactional
    public EventSchedule createEventSchedule(Long eventId,
                                             LocalDateTime startAt,
                                             LocalDateTime endAt,
                                             int totalSeatCount) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new TicketingException(EventErrorCode.EVENT_NOT_FOUND));

        validateCreateEventSchedule(startAt, endAt, totalSeatCount);

        // 같은 이벤트에 같은 시작 시각의 회차가 이미 있으면 중복 등록으로 본다.
        if (eventScheduleRepository.existsByEventIdAndStartAt(eventId, startAt)) {
            throw new TicketingException(EventScheduleErrorCode.EVENT_SCHEDULE_ALREADY_EXISTS);
        }

        EventSchedule schedule = EventSchedule.create(
                event,
                startAt,
                endAt,
                totalSeatCount
        );

        return eventScheduleRepository.save(schedule);
    }

    /**
     * 행사별 회차 목록 조회
     */
    @Transactional(readOnly = true)
    public List<EventSchedule> getEventSchedules(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new TicketingException(EventErrorCode.EVENT_NOT_FOUND);
        }

        return eventScheduleRepository.findAllByEventIdOrderByStartAtAsc(eventId);
    }

    /**
     * 회차 생성 시 비즈니스 규칙 검증
     */
    private void validateCreateEventSchedule(LocalDateTime startAt,
                                             LocalDateTime endAt,
                                             int totalSeatCount) {
        if (startAt == null || endAt == null) {
            throw new TicketingException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        if (!startAt.isBefore(endAt)) {
            throw new TicketingException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        if (totalSeatCount <= 0) {
            throw new TicketingException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}