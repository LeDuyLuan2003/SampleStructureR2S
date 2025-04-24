package com.r2s.ApiWebReview.repository;

import com.r2s.ApiWebReview.entity.VerificationToken;
import com.r2s.ApiWebReview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
