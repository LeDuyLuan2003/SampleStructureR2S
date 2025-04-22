package com.r2s.ApiWebReview.service;

import com.r2s.ApiWebReview.entity.RefreshToken;
import com.r2s.ApiWebReview.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
