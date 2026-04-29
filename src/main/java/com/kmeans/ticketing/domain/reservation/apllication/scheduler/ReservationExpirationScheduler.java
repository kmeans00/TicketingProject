package com.kmeans.ticketing.domain.reservation.application.scheduler;

import com.kmeans.ticketing.domain.reservation.application.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    public ReservationExpirationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * 일정 주기마다 미결제 만료 예매를 정리한다.
     *
     * fixedDelay:
     * - 이전 작업이 끝난 뒤 지정 시간 후 다시 실행
     *
     * 기본값은 1분(60000ms)
     */
    @Scheduled(fixedDelayString = "${reservation.expire-batch-fixed-delay-ms:60000}")
    public void expirePendingReservations() {
        int expiredCount = reservationService.expirePendingReservations();

        if (expiredCount > 0) {
            log.info("[RESERVATION-EXPIRE-BATCH] 만료 처리 완료. expiredCount={}", expiredCount);
        }
    }
}