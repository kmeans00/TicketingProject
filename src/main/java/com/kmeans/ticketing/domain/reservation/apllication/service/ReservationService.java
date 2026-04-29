package com.kmeans.ticketing.domain.reservation.application.service;

import com.kmeans.ticketing.domain.event.domain.EventSchedule;
import com.kmeans.ticketing.domain.event.infrastructure.EventScheduleRepository;
import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.payment.infrastructure.PaymentRepository;
import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;
import com.kmeans.ticketing.domain.reservation.exception.ReservationErrorCode;
import com.kmeans.ticketing.domain.reservation.infrastructure.ReservationRepository;
import com.kmeans.ticketing.domain.seat.domain.Seat;
import com.kmeans.ticketing.domain.seat.infrastructure.SeatRepository;
import com.kmeans.ticketing.domain.user.domain.User;
import com.kmeans.ticketing.domain.user.infrastructure.UserRepository;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kmeans.ticketing.domain.reservation.presentation.dto.ReservationSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private static final long PAYMENT_EXPIRE_MINUTES = 10;

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final EventScheduleRepository eventScheduleRepository;

    public ReservationService(UserRepository userRepository,
                              SeatRepository seatRepository,
                              ReservationRepository reservationRepository,
                              PaymentRepository paymentRepository,
                              EventScheduleRepository eventScheduleRepository) {
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.eventScheduleRepository = eventScheduleRepository;
    }

    /**
     * 예매 요청
     *
     * 처리 흐름:
     * 1. 사용자 조회
     * 2. 좌석 조회
     * 3. 좌석 예매 가능 여부 확인
     * 4. 회차 시작 여부 확인
     * 5. 좌석 RESERVED 처리
     * 6. availableSeatCount 감소
     * 7. Reservation 생성 (PENDING_PAYMENT)
     * 8. Payment 생성 (READY)
     */
    @Transactional
    public CreateReservationHolder createReservation(Long userId, Long seatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.RESERVATION_ACCESS_DENIED));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.SEAT_NOT_AVAILABLE));

        if (!seat.isAvailable()) {
            throw new TicketingException(ReservationErrorCode.SEAT_NOT_AVAILABLE);
        }

        EventSchedule schedule = seat.getSchedule();

        if (schedule.isStarted(LocalDateTime.now())) {
            throw new TicketingException(ReservationErrorCode.EVENT_SCHEDULE_ALREADY_STARTED);
        }

        // 좌석 선점
        // Seat 엔티티에 @Version 이 있으므로 같은 좌석 동시 요청 시
        // flush/commit 시점에 낙관적 락 충돌이 날 수 있다.
        seat.reserve();

        // 가용 좌석 수 원자 감소
        int updatedRowCount = eventScheduleRepository.decreaseAvailableSeatCount(schedule.getId());
        if (updatedRowCount == 0) {
            throw new TicketingException(ReservationErrorCode.SEAT_NOT_AVAILABLE);
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(PAYMENT_EXPIRE_MINUTES);

        Reservation reservation = Reservation.create(
                user,
                schedule,
                seat,
                seat.getPrice(),
                expiresAt
        );

        Reservation savedReservation = reservationRepository.save(reservation);

        Payment payment = Payment.create(savedReservation, savedReservation.getAmount());
        Payment savedPayment = paymentRepository.save(payment);

        // optimistic lock 예외를 트랜잭션 종료 직전이 아니라 최대한 빨리 발생시키기 위함
        reservationRepository.flush();

        return new CreateReservationHolder(savedReservation, savedPayment);
    }

    /**
     * 결제 Mock 성공 처리
     *
     * 처리 흐름:
     * 1. 본인 예매인지 확인
     * 2. 결제 정보 조회
     * 3. 예매 상태가 PENDING_PAYMENT인지 확인
     * 4. 만료 여부 확인
     * 5. Payment SUCCESS
     * 6. Reservation CONFIRMED
     * 7. Seat SOLD
     */
    @Transactional
    public CreateReservationHolder completePayment(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.PAYMENT_NOT_FOUND));

        if (!reservation.isPendingPayment()) {
            throw new TicketingException(ReservationErrorCode.RESERVATION_ALREADY_PROCESSED);
        }

        if (reservation.isExpired(LocalDateTime.now())) {
            reservation.expire();
            reservation.getSeat().release();
            reservation.getSchedule().increaseAvailableSeatCount();
            payment.cancel();
            throw new TicketingException(ReservationErrorCode.RESERVATION_EXPIRED);
        }

        if (!payment.isReady()) {
            throw new TicketingException(ReservationErrorCode.RESERVATION_ALREADY_PROCESSED);
        }

        payment.success();
        reservation.confirm();
        reservation.getSeat().sell();

        return new CreateReservationHolder(reservation, payment);
    }

    /**
     * 예매 취소
     *
     * 취소 가능 대상:
     * - PENDING_PAYMENT
     * - CONFIRMED
     *
     * 처리 흐름:
     * 1. 본인 예매인지 확인
     * 2. 결제 정보 조회
     * 3. 이미 취소/만료된 예매인지 확인
     * 4. 만료된 PENDING_PAYMENT면 EXPIRED 처리 후 예외 반환
     * 5. Reservation CANCELED
     * 6. Payment CANCELED
     * 7. Seat AVAILABLE
     * 8. availableSeatCount 증가
     */
    @Transactional
    public CreateReservationHolder cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.PAYMENT_NOT_FOUND));

        if (reservation.isCanceled() || reservation.isExpiredStatus()) {
            throw new TicketingException(ReservationErrorCode.RESERVATION_ALREADY_PROCESSED);
        }

        if (reservation.isPendingPayment() && reservation.isExpired(LocalDateTime.now())) {
            reservation.expire();
            reservation.getSeat().release();
            reservation.getSchedule().increaseAvailableSeatCount();
            payment.cancel();
            throw new TicketingException(ReservationErrorCode.RESERVATION_EXPIRED);
        }

        if (!reservation.isCancelable()) {
            throw new TicketingException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
        }

        reservation.cancel();
        payment.cancel();
        reservation.getSeat().release();
        reservation.getSchedule().increaseAvailableSeatCount();

        return new CreateReservationHolder(reservation, payment);
    }

    /**
     * 미결제 만료 예매 자동 정리
     *
     * 처리 흐름:
     * 1. PENDING_PAYMENT + expiresAt 지난 예매 조회
     * 2. Reservation EXPIRED
     * 3. Payment CANCELED
     * 4. Seat AVAILABLE
     * 5. availableSeatCount 증가
     */
    @Transactional
    public int expirePendingReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expiredReservations =
                reservationRepository.findExpiredReservations(ReservationStatus.PENDING_PAYMENT, now);

        int expiredCount = 0;

        for (Reservation reservation : expiredReservations) {
            Payment payment = paymentRepository.findByReservationId(reservation.getId())
                    .orElseThrow(() -> new TicketingException(ReservationErrorCode.PAYMENT_NOT_FOUND));

            if (!reservation.isPendingPayment()) {
                continue;
            }

            reservation.expire();
            payment.cancel();
            reservation.getSeat().release();
            reservation.getSchedule().increaseAvailableSeatCount();

            expiredCount++;
        }

        return expiredCount;
    }

    /**
     * 내 예매 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationSummaryResponse> getMyReservations(Long userId) {
        return reservationRepository.findMyReservationSummaries(userId);
    }

    /**
     * 내 예매 상세 조회
     */
    @Transactional(readOnly = true)
    public Reservation getMyReservation(Long userId, Long reservationId) {
        return reservationRepository.findDetailByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new TicketingException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    /**
     * Reservation + Payment를 같이 응답 DTO로 만들기 위한 내부 홀더
     */
    public record CreateReservationHolder(
            Reservation reservation,
            Payment payment
    ) {
    }
}