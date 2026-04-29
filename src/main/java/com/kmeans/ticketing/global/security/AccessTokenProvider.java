package com.kmeans.ticketing.global.security;

import com.kmeans.ticketing.domain.user.domain.User;

public interface AccessTokenProvider {

    String createAccessToken(User user);
}