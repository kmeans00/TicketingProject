package com.kmeans.ticketing.global.config;

import com.kmeans.ticketing.domain.user.domain.AuthProvider;
import com.kmeans.ticketing.domain.user.domain.User;
import com.kmeans.ticketing.domain.user.infrastructure.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 관리자 계정이 없으면 자동 생성한다.
 */
@Slf4j
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public AdminAccountInitializer(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 같은 이메일의 LOCAL 관리자/계정 존재 여부 확인
        boolean exists = userRepository.existsByEmailAndAuthProvider(
                adminProperties.email(),
                AuthProvider.LOCAL
        );

        if (exists) {
            log.info("[ADMIN-INIT] 관리자 계정이 이미 존재합니다. email={}", adminProperties.email());
            return;
        }

        // application.yml에 적은 비밀번호를 BCrypt로 암호화
        String encodedPassword = passwordEncoder.encode(adminProperties.password());

        User adminUser = User.createAdminUser(
                adminProperties.email(),
                encodedPassword,
                adminProperties.name()
        );

        userRepository.save(adminUser);

        log.info("[ADMIN-INIT] 관리자 계정 자동 생성 완료. email={}", adminProperties.email());
    }
}