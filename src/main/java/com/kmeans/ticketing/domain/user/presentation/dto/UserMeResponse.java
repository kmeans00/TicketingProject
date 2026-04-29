package com.kmeans.ticketing.domain.user.presentation.dto;

import com.kmeans.ticketing.domain.user.domain.Role;

/**
 * 현재 로그인한 사용자 정보 응답 DTO
 */
public record UserMeResponse(
        Long userId,
        String email,
        Role role
) {
}