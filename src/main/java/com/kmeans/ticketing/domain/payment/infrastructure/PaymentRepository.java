package com.kmeans.ticketing.domain.payment.infrastructure;

import com.kmeans.ticketing.domain.payment.domain.Payment;
import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.settlement.application.dto.SettlementAggregateResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 특정 예매의 결제 정보 조회
     */
    Optional<Payment> findByReservationId(Long reservationId);

    @Query("""
        select new com.kmeans.ticketing.domain.settlement.application.dto.SettlementAggregateResult(
            count(p),
            coalesce(sum(p.amount), 0)
        )
        from Payment p
        where p.status = :status
          and p.paidAt >= :start
          and p.paidAt < :end
        """)
    SettlementAggregateResult aggregateSettlement(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}