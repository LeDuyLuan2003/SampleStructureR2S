package com.r2s.ApiWebReview.service;

import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> register(RegisterRequest request);
    ResponseEntity<?> login(LoginRequest request);
}
