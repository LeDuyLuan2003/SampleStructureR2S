package com.r2s.ApiWebReview.service.impl;

import com.r2s.ApiWebReview.common.annotation.LogExecutionTime;
import com.r2s.ApiWebReview.common.enums.RoleEnum;
import com.r2s.ApiWebReview.common.event.UserRegisteredEvent;
import com.r2s.ApiWebReview.common.util.JwtUtil;
import com.r2s.ApiWebReview.dto.AuthResponse;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.entity.RefreshToken;
import com.r2s.ApiWebReview.entity.Role;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.exception.type.ResourceNotFoundException;
import com.r2s.ApiWebReview.exception.type.UnauthorizedException;
import com.r2s.ApiWebReview.repository.RoleRepository;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @LogExecutionTime
    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            //log.warn("Cố gắng đăng ký bằng email hiện có: {}", request.getEmail());
            throw new BadRequestException("Email đã được đăng ký.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role USER"));
        user.setRole(role);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(user));
        return user;
    }

    @Override
    @LogExecutionTime
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không đúng."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Email hoặc mật khẩu không đúng.");
        }

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthResponse(accessToken, System.currentTimeMillis() + jwtUtil.getExpirationTime());
    }

    @Override
    public AuthResponse refreshAccessToken(String tokenValue) {
        RefreshToken token = refreshTokenService.findByToken(tokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token không hợp lệ"));

        refreshTokenService.verifyExpiration(token);
        String newAccessToken = jwtUtil.generateToken(token.getUser());

        return new AuthResponse(newAccessToken, System.currentTimeMillis() + jwtUtil.getExpirationTime());
    }

    @Override
    public void logout(String token, HttpServletResponse response) {
        refreshTokenService.findByToken(token)
                .ifPresent(t -> refreshTokenService.deleteByUser(t.getUser()));

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }


}
