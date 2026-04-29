package com.kmeans.ticketing.domain.user.application.service;

import com.kmeans.ticketing.domain.user.application.dto.LoginCommand;
import com.kmeans.ticketing.domain.user.application.dto.LoginResult;
import com.kmeans.ticketing.domain.user.domain.AuthProvider;

public interface LoginProcessor {

    AuthProvider supports();

    LoginResult login(LoginCommand command);
}