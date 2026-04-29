package com.kmeans.ticketing.global.security;

import com.kmeans.ticketing.domain.user.domain.Role;

/**
 * JWT 안에서 꺼낸 현재 로그인 사용자 정보
 * SecurityContext의 principal로 넣어서 사용한다.
 */
public record JwtUserInfo(
        Long userId,
        String email,
        Role role
) {
}