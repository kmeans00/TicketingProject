package com.kmeans.ticketing.domain.user.presentation.controller;

import com.kmeans.ticketing.domain.user.presentation.dto.UserMeResponse;
import com.kmeans.ticketing.global.response.ApiResponse;
import com.kmeans.ticketing.global.security.JwtUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 확인용 사용자 API
 */
@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "내 정보 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> me(
            @Parameter(hidden = true)
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        UserMeResponse response = new UserMeResponse(
                userInfo.userId(),
                userInfo.email(),
                userInfo.role()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "USER_ME_SUCCESS",
                        "내 정보 조회에 성공했습니다.",
                        response
                )
        );
    }
}