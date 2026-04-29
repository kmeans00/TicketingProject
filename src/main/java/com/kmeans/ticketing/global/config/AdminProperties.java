package com.kmeans.ticketing.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 앱 시작 시 자동 생성할 관리자 계정 설정값
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String email,
        String password,
        String name
) {
}