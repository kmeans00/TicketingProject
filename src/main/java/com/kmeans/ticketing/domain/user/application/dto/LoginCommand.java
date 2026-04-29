package com.kmeans.ticketing.domain.user.application.dto;

import com.kmeans.ticketing.domain.user.domain.AuthProvider;

public record LoginCommand(
        AuthProvider provider,
        String email,
        String password,
        String socialAccessToken
) {
}