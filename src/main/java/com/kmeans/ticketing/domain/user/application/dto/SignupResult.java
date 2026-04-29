package com.kmeans.ticketing.domain.user.application.dto;

import com.kmeans.ticketing.domain.user.domain.Role;
import com.kmeans.ticketing.domain.user.domain.User;

/**
 * 회원가입 결과를 담는 application 계층용 DTO
 * 엔티티를 그대로 밖으로 내보내지 않기 위해 따로 둔다.
 */
public record SignupResult(
        Long userId,
        String email,
        String name,
        Role role
) {
    public static SignupResult from(User user) {
        return new SignupResult(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}