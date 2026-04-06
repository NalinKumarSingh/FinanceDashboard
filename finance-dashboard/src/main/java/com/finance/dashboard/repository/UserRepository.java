package com.finance.dashboard.repository;

import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAllByStatus(UserStatus status);
}
