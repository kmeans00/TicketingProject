package com.kmeans.ticketing.domain.user.application.service;

import com.kmeans.ticketing.domain.user.application.dto.LoginCommand;
import com.kmeans.ticketing.domain.user.application.dto.LoginResult;
import com.kmeans.ticketing.domain.user.application.dto.SignupCommand;
import com.kmeans.ticketing.domain.user.application.dto.SignupResult;
import com.kmeans.ticketing.domain.user.domain.AuthProvider;
import com.kmeans.ticketing.domain.user.domain.User;
import com.kmeans.ticketing.domain.user.exception.AuthErrorCode;
import com.kmeans.ticketing.domain.user.infrastructure.UserRepository;
import com.kmeans.ticketing.global.exception.TicketingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final Map<AuthProvider, LoginProcessor> loginProcessorMap;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 스프링이 LoginProcessor 구현체(LocalLoginProcessor 등)를 모두 주입해준다.
     * 그리고 회원가입에 필요한 repository, passwordEncoder도 같이 주입받는다.
     */
    public AuthService(List<LoginProcessor> loginProcessors,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.loginProcessorMap = loginProcessors.stream()
                .collect(Collectors.toUnmodifiableMap(LoginProcessor::supports, Function.identity()));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 로그인 방식(provider)에 맞는 LoginProcessor를 찾아 위임한다.
     */
    public LoginResult login(LoginCommand command) {
        LoginProcessor processor = loginProcessorMap.get(command.provider());

        if (processor == null) {
            throw new TicketingException(AuthErrorCode.PROVIDER_NOT_SUPPORTED);
        }

        return processor.login(command);
    }

    /**
     * 로컬 회원가입 처리
     * 1. 중복 이메일 확인
     * 2. 비밀번호 암호화
     * 3. LOCAL 사용자 생성
     * 4. 저장 후 결과 반환
     */
    public SignupResult signup(SignupCommand command) {
        // 같은 이메일의 LOCAL 계정이 이미 있으면 회원가입 불가
        if (userRepository.existsByEmailAndAuthProvider(command.email(), AuthProvider.LOCAL)) {
            throw new TicketingException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 평문 비밀번호를 그대로 저장하면 안 되므로 반드시 암호화
        String encodedPassword = passwordEncoder.encode(command.password());

        // User 엔티티의 정적 팩토리 메서드로 로컬 사용자 생성
        User user = User.createLocalUser(
                command.email(),
                encodedPassword,
                command.name()
        );

        // DB 저장
        User savedUser = userRepository.save(user);

        // application 계층용 결과 객체로 변환해서 반환
        return SignupResult.from(savedUser);
    }
}