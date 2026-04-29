package com.kmeans.ticketing.domain.user.code;

import com.kmeans.ticketing.global.response.ApiSuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthSuccessCode implements ApiSuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_LOGIN_SUCCESS", "로그인에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH_SIGNUP_SUCCESS", "회원가입에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthSuccessCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
