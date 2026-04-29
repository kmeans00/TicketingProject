package com.kmeans.ticketing.domain.user.presentation.dto;

import com.kmeans.ticketing.domain.user.domain.AuthProvider;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull(message = "로그인 방식은 필수입니다.")
        AuthProvider provider,

        String email,

        String password,

        String socialAccessToken
) {
}