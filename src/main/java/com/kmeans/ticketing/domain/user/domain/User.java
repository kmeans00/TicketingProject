package com.kmeans.ticketing.domain.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider authProvider;

    @Column(length = 255)
    private String providerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String password, String name, Role role,
                 AuthProvider authProvider, String providerId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.authProvider = authProvider;
        this.providerId = providerId;
    }

    public static User createLocalUser(String email, String encodedPassword, String name) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .providerId(null)
                .build();
    }

    /**
     * 개발용/초기 세팅용 관리자 계정 생성
     */
    public static User createAdminUser(String email, String encodedPassword, String name) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(Role.ADMIN)
                .authProvider(AuthProvider.LOCAL)
                .providerId(null)
                .build();
    }

    public static User createSocialUser(String email, String name, AuthProvider authProvider, String providerId) {
        return User.builder()
                .email(email)
                .password(null)
                .name(name)
                .role(Role.USER)
                .authProvider(authProvider)
                .providerId(providerId)
                .build();
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isLocalAccount() {
        return this.authProvider == AuthProvider.LOCAL;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}