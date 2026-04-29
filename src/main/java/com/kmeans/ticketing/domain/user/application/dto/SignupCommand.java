package com.kmeans.ticketing.domain.user.application.dto;

/**
 * 회원가입 유스케이스 실행용 객체
 * Controller에서 받은 요청값을 Service에 넘길 때 사용한다.
 */
public record SignupCommand(
        String email,
        String password,
        String name
) {
}