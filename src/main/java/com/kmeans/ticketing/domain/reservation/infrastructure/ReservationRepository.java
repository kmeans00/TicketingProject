package com.kmeans.ticketing.domain.reservation.infrastructure;

import com.kmeans.ticketing.domain.reservation.domain.Reservation;
import com.kmeans.ticketing.domain.reservation.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    /**
     * 특정 사용자의 특정 예매 조회
     */
    Optional<Reservation> findByIdAndUserId(Long reservationId, Long userId);

    /**
     * 결제 대기 상태이면서 만료 시간이 지난 예매 조회
     */
    @Query("""
            select r
            from Reservation r
            join fetch r.seat s
            join fetch r.schedule sc
            where r.status = :status
              and r.expiresAt < :now
            """)
    List<Reservation> findExpiredReservations(
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * 내 예매 상세 조회
     */
    @Query("""
            select r
            from Reservation r
            join fetch r.seat s
            join fetch r.schedule sc
            join fetch sc.event e
            left join fetch r.payment p
            where r.id = :reservationId
              and r.user.id = :userId
            """)
    Optional<Reservation> findDetailByIdAndUserId(
            @Param("reservationId") Long reservationId,
            @Param("userId") Long userId
    );
}