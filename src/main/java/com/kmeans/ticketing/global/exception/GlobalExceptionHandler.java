package com.kmeans.ticketing.global.exception;

import com.kmeans.ticketing.domain.common.exception.CommonErrorCode;
import com.kmeans.ticketing.domain.reservation.exception.ReservationErrorCode;
import com.kmeans.ticketing.global.response.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 커스텀 예외 처리
     */
    @ExceptionHandler(TicketingException.class)
    public ResponseEntity<ApiResponse<Void>> handleTicketingException(TicketingException e) {
        ApiErrorCode errorCode = e.getErrorCode();

        log.warn(
                "[TicketingException] code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage(),
                e
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    /**
     * 낙관적 락 충돌 예외 처리
     *
     * 같은 좌석을 동시에 예매하려고 할 때 발생할 수 있다.
     * 기본 500이 아니라 의미 있는 409 응답으로 변환한다.
     */
    @ExceptionHandler({
            OptimisticLockException.class,
            ObjectOptimisticLockingFailureException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockException(Exception e) {
        ReservationErrorCode errorCode = ReservationErrorCode.SEAT_RESERVATION_CONFLICT;

        log.warn(
                "[OptimisticLockException] code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage(),
                e
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    /**
     * @Valid 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : CommonErrorCode.INVALID_INPUT_VALUE.getMessage();

        log.warn("[ValidationException] message={}", message, e);

        return ResponseEntity
                .status(CommonErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.fail(
                        CommonErrorCode.INVALID_INPUT_VALUE.getCode(),
                        message
                ));
    }

    /**
     * 예상하지 못한 서버 내부 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[UnhandledException] unexpected error occurred", e);

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}