package com.r2s.ApiWebReview.service;

import com.r2s.ApiWebReview.dto.AuthResponse;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    User register(RegisterRequest request);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse refreshAccessToken(String tokenValue);
    void logout(String token, HttpServletResponse response);

}
