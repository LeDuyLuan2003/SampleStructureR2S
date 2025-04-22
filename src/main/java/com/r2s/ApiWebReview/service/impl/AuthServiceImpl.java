package com.r2s.ApiWebReview.service.impl;

import com.r2s.ApiWebReview.common.util.JwtUtil;
import com.r2s.ApiWebReview.dto.AuthResponse;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.entity.RefreshToken;
import com.r2s.ApiWebReview.entity.Role;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.repository.RoleRepository;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã được đăng ký.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gán role mặc định là USER
        Role role = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Không tìm thấy role USER"));
        user.setRole(role);

        userRepository.save(user);
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Email hoặc mật khẩu không đúng.");
        }

        // Sinh access token
        String accessToken = jwtUtil.generateToken(user.getEmail());

        // Tạo và lưu refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Set cookie HttpOnly cho web
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // set true nếu dùng HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Trả access token cho client (web/mobile dùng)
        return ResponseEntity.ok(new AuthResponse(accessToken, System.currentTimeMillis() + jwtUtil.getExpirationTime()));
    }

    @Override
    public ResponseEntity<?> refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        // Kiểm tra hạn sử dụng
        refreshTokenService.verifyExpiration(refreshToken);

        // Sinh access token mới từ user
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(newAccessToken, System.currentTimeMillis() + jwtUtil.getExpirationTime()));
    }

    @Override
    public ResponseEntity<?> logout(String refreshToken, HttpServletResponse response) {
        // Tìm và xoá token trong DB
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenService.deleteByUser(token.getUser()));

        // Tạo cookie xoá bỏ
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)              // nên match với login
                .path("/")
                .maxAge(0)                  // xoá cookie
                .sameSite("Strict")         // thêm dòng này
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok("Đăng xuất thành công.");
    }

}
