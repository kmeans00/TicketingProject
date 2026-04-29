package com.kmeans.ticketing.domain.user.infrastructure;

import com.kmeans.ticketing.domain.user.domain.AuthProvider;
import com.kmeans.ticketing.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    boolean existsByEmailAndAuthProvider(String email, AuthProvider authProvider);
}