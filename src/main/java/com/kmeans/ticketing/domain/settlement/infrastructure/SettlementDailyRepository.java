package com.kmeans.ticketing.domain.settlement.infrastructure;

import com.kmeans.ticketing.domain.settlement.domain.SettlementDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementDailyRepository extends JpaRepository<SettlementDaily, Long> {

    Optional<SettlementDaily> findBySettlementDate(LocalDate settlementDate);

    List<SettlementDaily> findAllBySettlementDateBetweenOrderBySettlementDateDesc(LocalDate from, LocalDate to);
}