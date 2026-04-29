package com.kmeans.ticketing.domain.reservation.infrastructure;

import com.kmeans.ticketing.domain.event.domain.QEvent;
import com.kmeans.ticketing.domain.event.domain.QEventSchedule;
import com.kmeans.ticketing.domain.payment.domain.QPayment;
import com.kmeans.ticketing.domain.reservation.domain.QReservation;
import com.kmeans.ticketing.domain.reservation.presentation.dto.ReservationSummaryResponse;
import com.kmeans.ticketing.domain.seat.domain.QSeat;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReservationRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<ReservationSummaryResponse> findMyReservationSummaries(Long userId) {
        QReservation reservation = QReservation.reservation;
        QPayment payment = QPayment.payment;
        QSeat seat = QSeat.seat;
        QEventSchedule schedule = QEventSchedule.eventSchedule;
        QEvent event = QEvent.event;

        return queryFactory
                .select(Projections.constructor(
                        ReservationSummaryResponse.class,
                        reservation.id,
                        event.id,
                        event.title,
                        schedule.id,
                        schedule.startAt,
                        seat.id,
                        seat.seatNo,
                        seat.zone,
                        reservation.amount,
                        reservation.status,
                        payment.status,
                        reservation.createdAt
                ))
                .from(reservation)
                .join(reservation.seat, seat)
                .join(reservation.schedule, schedule)
                .join(schedule.event, event)
                .leftJoin(reservation.payment, payment)
                .where(reservation.user.id.eq(userId))
                .orderBy(reservation.createdAt.desc())
                .fetch();
    }
}