package com.kmeans.ticketing.domain.user.application.service;

import com.kmeans.ticketing.domain.user.application.dto.LoginCommand;
import com.kmeans.ticketing.domain.user.application.dto.LoginResult;
import com.kmeans.ticketing.domain.user.domain.AuthProvider;
import com.kmeans.ticketing.domain.user.domain.User;
import com.kmeans.ticketing.domain.user.exception.AuthErrorCode;
import com.kmeans.ticketing.domain.user.infrastructure.UserRepository;
import com.kmeans.ticketing.global.exception.TicketingException;
import com.kmeans.ticketing.global.security.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LocalLoginProcessor implements LoginProcessor {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenProvider accessTokenProvider;

    @Override
    public AuthProvider supports() {
        return AuthProvider.LOCAL;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        validate(command);

        User user = userRepository.findByEmailAndAuthProvider(command.email(), AuthProvider.LOCAL)
                .orElseThrow(() -> new TicketingException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!user.isLocalAccount()) {
            throw new TicketingException(AuthErrorCode.LOCAL_ACCOUNT_ONLY);
        }

        if (user.getPassword() == null || !passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new TicketingException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = accessTokenProvider.createAccessToken(user);
        return LoginResult.from(user, accessToken);
    }

    private void validate(LoginCommand command) {
        if (!StringUtils.hasText(command.email())) {
            throw new TicketingException(AuthErrorCode.EMAIL_REQUIRED);
        }

        if (!StringUtils.hasText(command.password())) {
            throw new TicketingException(AuthErrorCode.PASSWORD_REQUIRED);
        }
    }
}
