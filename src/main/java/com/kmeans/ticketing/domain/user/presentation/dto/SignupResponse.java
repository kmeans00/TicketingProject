package com.kmeans.ticketing.domain.user.presentation.dto;

import com.kmeans.ticketing.domain.user.application.dto.SignupResult;
import com.kmeans.ticketing.domain.user.domain.Role;

/**
 * 클라이언트에게 내려줄 회원가입 응답 DTO
 */
public record SignupResponse(
        Long userId,
        String email,
        String name,
        Role role
) {
    public static SignupResponse from(SignupResult result) {
        return new SignupResponse(
                result.userId(),
                result.email(),
                result.name(),
                result.role()
        );
    }
}