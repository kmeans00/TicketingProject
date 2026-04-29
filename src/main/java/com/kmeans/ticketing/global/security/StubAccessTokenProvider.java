//package com.kmeans.ticketing.global.security;
//
//import com.kmeans.ticketing.domain.user.domain.User;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class StubAccessTokenProvider implements AccessTokenProvider {
//
//    @Override
//    public String createAccessToken(User user) {
//        return "temp-access-token-" + user.getId() + "-" + UUID.randomUUID();
//    }
//}