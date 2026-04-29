package com.kmeans.ticketing.domain.reservation.infrastructure;

import com.kmeans.ticketing.domain.reservation.presentation.dto.ReservationSummaryResponse;

import java.util.List;

public interface ReservationRepositoryCustom {

    List<ReservationSummaryResponse> findMyReservationSummaries(Long userId);
}