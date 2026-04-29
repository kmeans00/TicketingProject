package com.kmeans.ticketing.global.security;

import com.kmeans.ticketing.domain.user.domain.Role;
import com.kmeans.ticketing.domain.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * JWT 생성/검증을 담당하는 클래스
 */
@Component
public class JwtAccessTokenProvider implements AccessTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtAccessTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        // secret 문자열을 바이트로 변환해서 서명 키 생성
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 로그인 성공 시 access token 생성
     */
    @Override
    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiredAt = now.plusSeconds(jwtProperties.accessTokenExpirationSeconds());

        return Jwts.builder()
                // JWT subject. 일반적으로 사용자 식별용 문자열을 넣는다.
                .subject(String.valueOf(user.getId()))
                // 커스텀 claim들
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiredAt))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Authorization 헤더에서 꺼낸 토큰이 유효한지 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 현재 사용자 정보를 꺼내서 SecurityContext에 넣기 위해 사용
     */
    public JwtUserInfo getUserInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));

        return new JwtUserInfo(userId, email, role);
    }
}