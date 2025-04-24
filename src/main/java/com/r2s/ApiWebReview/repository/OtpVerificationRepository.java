package com.r2s.ApiWebReview.repository;

import com.r2s.ApiWebReview.entity.OtpVerification;
import com.r2s.ApiWebReview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByUser(User user);
    Optional<OtpVerification> findByOtp(String otp);
    void deleteByUser(User user);
}
