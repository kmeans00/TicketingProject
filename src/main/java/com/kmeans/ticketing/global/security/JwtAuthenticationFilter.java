package com.kmeans.ticketing.global.security;

import com.kmeans.ticketing.domain.user.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 모든 요청에서 Authorization 헤더의 Bearer 토큰을 검사하는 필터
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAccessTokenProvider jwtAccessTokenProvider;

    public JwtAuthenticationFilter(JwtAccessTokenProvider jwtAccessTokenProvider) {
        this.jwtAccessTokenProvider = jwtAccessTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);

        // 토큰이 있고, 유효하면 SecurityContext에 인증 정보 저장
        if (StringUtils.hasText(accessToken) && jwtAccessTokenProvider.validateToken(accessToken)) {
            JwtUserInfo userInfo = jwtAccessTokenProvider.getUserInfo(accessToken);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userInfo, // principal
                            null,     // credentials
                            List.of(new SimpleGrantedAuthority("ROLE_" + userInfo.role().name()))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization: Bearer xxx 에서 실제 토큰 문자열만 꺼낸다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken)) {
            return null;
        }

        if (!bearerToken.startsWith("Bearer ")) {
            return null;
        }

        return bearerToken.substring(7);
    }

    /**
     * 로그인/회원가입 같은 인증 전 API는 토큰 검사를 생략해도 된다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/v1/auth/");
    }
}