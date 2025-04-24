package com.r2s.ApiWebReview.service.impl;

import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.entity.VerificationToken;
import com.r2s.ApiWebReview.repository.VerificationTokenRepository;
import com.r2s.ApiWebReview.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 phút

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken createToken(User user) {
        tokenRepository.deleteByUser(user);

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(EXPIRATION_MS));

        return tokenRepository.save(token);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteByUser(User user) {
        tokenRepository.deleteByUser(user);
    }
}
