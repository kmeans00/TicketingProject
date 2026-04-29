package com.kmeans.ticketing.global.response;

import com.kmeans.ticketing.global.exception.ApiErrorCode;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(ApiSuccessCode successCode, T data) {
        return new ApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                data
        );
    }

    public static ApiResponse<Void> fail(ApiErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null
        );
    }
}
