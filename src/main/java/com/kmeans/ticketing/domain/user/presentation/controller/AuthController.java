package com.kmeans.ticketing.domain.user.presentation.controller;

import com.kmeans.ticketing.domain.user.application.dto.LoginCommand;
import com.kmeans.ticketing.domain.user.application.dto.LoginResult;
import com.kmeans.ticketing.domain.user.application.dto.SignupCommand;
import com.kmeans.ticketing.domain.user.application.dto.SignupResult;
import com.kmeans.ticketing.domain.user.application.service.AuthService;
import com.kmeans.ticketing.domain.user.code.AuthSuccessCode;
import com.kmeans.ticketing.domain.user.presentation.dto.LoginRequest;
import com.kmeans.ticketing.domain.user.presentation.dto.LoginResponse;
import com.kmeans.ticketing.domain.user.presentation.dto.SignupRequest;
import com.kmeans.ticketing.domain.user.presentation.dto.SignupResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하고 JWT access token을 발급합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "이메일 또는 비밀번호 불일치"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = authService.login(
                new LoginCommand(
                        request.provider(),
                        request.email(),
                        request.password(),
                        request.socialAccessToken()
                )
        );

        LoginResponse response = LoginResponse.from(result);

        return ResponseEntity
                .status(AuthSuccessCode.LOGIN_SUCCESS.getStatus())
                .body(ApiResponse.success(AuthSuccessCode.LOGIN_SUCCESS, response));
    }

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 이름으로 로컬 회원가입을 진행합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 이메일"
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResult result = authService.signup(
                new SignupCommand(
                        request.email(),
                        request.password(),
                        request.name()
                )
        );

        SignupResponse response = SignupResponse.from(result);

        return ResponseEntity
                .status(AuthSuccessCode.SIGNUP_SUCCESS.getStatus())
                .body(ApiResponse.success(AuthSuccessCode.SIGNUP_SUCCESS, response));
    }
}