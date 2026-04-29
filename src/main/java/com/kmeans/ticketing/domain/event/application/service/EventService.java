package com.kmeans.ticketing.domain.event.application.service;

import com.kmeans.ticketing.domain.common.exception.CommonErrorCode;
import com.kmeans.ticketing.domain.event.application.dto.CreateEventCommand;
import com.kmeans.ticketing.domain.event.application.dto.CreateEventResult;
import com.kmeans.ticketing.domain.event.domain.Event;
import com.kmeans.ticketing.domain.event.domain.EventStatus;
import com.kmeans.ticketing.domain.event.exception.EventErrorCode;
import com.kmeans.ticketing.domain.event.infrastructure.EventRepository;
import com.kmeans.ticketing.domain.event.presentation.dto.EventDetailResponse;
import com.kmeans.ticketing.domain.event.presentation.dto.EventSummaryResponse;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * 관리자 행사 등록
     *
     * 새 행사가 등록되면 행사 목록이 바뀌므로
     * eventListCache는 전체 비운다.
     */
    @Transactional
    @CacheEvict(value = "eventListCache", allEntries = true)
    public CreateEventResult createEvent(CreateEventCommand command) {
        validateCreateEvent(command);

        Event event = Event.create(
                command.title(),
                command.description(),
                command.location(),
                command.bookingOpenAt(),
                command.bookingCloseAt()
        );

        Event savedEvent = eventRepository.save(event);

        return CreateEventResult.from(savedEvent);
    }

    /**
     * 행사 목록 조회
     *
     * status가 없으면 전체 조회
     * status가 있으면 해당 상태의 행사만 조회
     *
     * 엔티티가 아니라 DTO를 캐시한다.
     *
     * 캐시 키 예시:
     * - status 없음 -> ALL
     * - status=OPEN -> OPEN
     */
    @Transactional(readOnly = true)
    @Cacheable(
            value = "eventListCache",
            key = "#status == null ? 'ALL' : #status.name()"
    )
    public List<EventSummaryResponse> getEventSummaries(EventStatus status) {
        List<Event> events;

        if (status == null) {
            events = eventRepository.findAllByOrderByCreatedAtDesc();
        } else {
            events = eventRepository.findAllByStatusOrderByCreatedAtDesc(status);
        }

        return events.stream()
                .map(EventSummaryResponse::from)
                .toList();
    }

    /**
     * 행사 상세 조회
     *
     * eventId로 행사 1건을 조회하고,
     * 없으면 EventErrorCode.EVENT_NOT_FOUND 예외를 던진다.
     *
     * 엔티티가 아니라 DTO를 캐시한다.
     *
     * 캐시 키:
     * - eventId
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "eventDetailCache", key = "#eventId")
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new TicketingException(EventErrorCode.EVENT_NOT_FOUND));

        return EventDetailResponse.from(event);
    }

    /**
     * 단순 @Valid 검증 외에 비즈니스 규칙 검증
     */
    private void validateCreateEvent(CreateEventCommand command) {
        if (command.bookingOpenAt().isAfter(command.bookingCloseAt())
                || command.bookingOpenAt().isEqual(command.bookingCloseAt())) {
            throw new TicketingException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}