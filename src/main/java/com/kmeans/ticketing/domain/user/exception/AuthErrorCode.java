package com.kmeans.ticketing.domain.user.exception;

import com.kmeans.ticketing.global.exception.ApiErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ApiErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_EMAIL_REQUIRED", "이메일은 필수입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH_PASSWORD_REQUIRED", "비밀번호는 필수입니다."),
    PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "AUTH_PROVIDER_NOT_SUPPORTED", "지원하지 않는 로그인 방식입니다."),
    LOCAL_ACCOUNT_ONLY(HttpStatus.BAD_REQUEST, "AUTH_LOCAL_ACCOUNT_ONLY", "로컬 계정이 아닙니다."),

    // 회원가입 시 이미 같은 이메일의 로컬 계정이 있으면 발생시키는 에러
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}