package com.kmeans.ticketing.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청에 대해 공통 로그를 남기는 필터
 *
 * 남기는 정보:
 * - HTTP Method
 * - Request URI
 * - Query String
 * - Response Status
 * - 처리 시간(ms)
 * - Client IP
 *
 * 주의:
 * - 로그인/회원가입 요청 body 자체는 로그로 남기지 않는다.
 *   비밀번호, 토큰 같은 민감정보가 포함될 수 있기 때문.
 */
@Slf4j
@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);

        try {
            // 다음 필터 또는 컨트롤러로 요청 전달
            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            int status = response.getStatus();

            // query string이 있으면 같이 붙여서 보기 좋게 출력
            String fullUrl = (queryString == null || queryString.isBlank())
                    ? uri
                    : uri + "?" + queryString;

            log.info("[API] method={}, url={}, status={}, elapsedMs={}, clientIp={}",
                    method, fullUrl, status, elapsedTime, clientIp);
        }
    }

    /**
     * 프록시/로드밸런서를 거치는 환경까지 고려한 IP 조회
     * 지금은 로컬 개발이라 127.0.0.1 또는 localhost 계열이 주로 찍힐 것.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * 정적 리소스나 불필요한 요청은 로그에서 제외할 수 있다.
     * 지금은 예시로 favicon 정도만 제외.
     *
     * 필요하면 swagger, css, js, image 경로도 제외 가능.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/favicon.ico");
    }
}