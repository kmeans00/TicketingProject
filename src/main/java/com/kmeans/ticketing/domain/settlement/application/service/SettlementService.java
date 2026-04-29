package com.kmeans.ticketing.domain.settlement.application.service;

import com.kmeans.ticketing.domain.payment.domain.PaymentStatus;
import com.kmeans.ticketing.domain.payment.infrastructure.PaymentRepository;
import com.kmeans.ticketing.domain.settlement.application.dto.SettlementAggregateResult;
import com.kmeans.ticketing.domain.settlement.domain.SettlementDaily;
import com.kmeans.ticketing.domain.settlement.exception.SettlementErrorCode;
import com.kmeans.ticketing.domain.settlement.infrastructure.SettlementDailyRepository;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SettlementService {

    private final PaymentRepository paymentRepository;
    private final SettlementDailyRepository settlementDailyRepository;

    public SettlementService(PaymentRepository paymentRepository,
                             SettlementDailyRepository settlementDailyRepository) {
        this.paymentRepository = paymentRepository;
        this.settlementDailyRepository = settlementDailyRepository;
    }

    /**
     * 특정 날짜의 일별 정산 집계
     *
     * 기준:
     * - Payment.status = SUCCESS
     * - paidAt 이 해당 날짜 범위에 포함되는 건
     */
    @Transactional
    public SettlementDaily settleDaily(LocalDate settlementDate) {
        LocalDateTime start = settlementDate.atStartOfDay();
        LocalDateTime end = settlementDate.plusDays(1).atStartOfDay();

        SettlementAggregateResult aggregate = paymentRepository.aggregateSettlement(
                PaymentStatus.SUCCESS,
                start,
                end
        );

        SettlementDaily settlementDaily = settlementDailyRepository.findBySettlementDate(settlementDate)
                .orElseGet(() -> settlementDailyRepository.save(
                        SettlementDaily.create(settlementDate, 0, 0)
                ));

        settlementDaily.updateSummary(
                aggregate.paymentCount(),
                aggregate.totalAmount()
        );

        return settlementDaily;
    }

    /**
     * 일별 정산 1건 조회
     */
    @Transactional(readOnly = true)
    public SettlementDaily getSettlementDaily(LocalDate settlementDate) {
        return settlementDailyRepository.findBySettlementDate(settlementDate)
                .orElseThrow(() -> new TicketingException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));
    }

    /**
     * 일별 정산 기간 조회
     */
    @Transactional(readOnly = true)
    public List<SettlementDaily> getSettlementDailyList(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new TicketingException(SettlementErrorCode.INVALID_SETTLEMENT_RANGE);
        }

        return settlementDailyRepository.findAllBySettlementDateBetweenOrderBySettlementDateDesc(from, to);
    }
}