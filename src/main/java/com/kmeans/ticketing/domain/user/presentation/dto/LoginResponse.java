package com.kmeans.ticketing.domain.user.presentation.dto;

import com.kmeans.ticketing.domain.user.application.dto.LoginResult;
import com.kmeans.ticketing.domain.user.domain.Role;

public record LoginResponse(
        Long userId,
        String name,
        String email,
        Role role,
        String accessToken
) {
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(
                result.userId(),
                result.name(),
                result.email(),
                result.role(),
                result.accessToken()
        );
    }
}