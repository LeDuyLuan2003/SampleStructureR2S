package com.r2s.ApiWebReview.service;

import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    VerificationToken createToken(User user);
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);
}
