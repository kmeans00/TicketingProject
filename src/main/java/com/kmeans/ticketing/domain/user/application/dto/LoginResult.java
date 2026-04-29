package com.kmeans.ticketing.domain.user.application.dto;

import com.kmeans.ticketing.domain.user.domain.Role;
import com.kmeans.ticketing.domain.user.domain.User;

public record LoginResult(
        Long userId,
        String name,
        String email,
        Role role,
        String accessToken
) {
    public static LoginResult from(User user, String accessToken) {
        return new LoginResult(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken
        );
    }
}