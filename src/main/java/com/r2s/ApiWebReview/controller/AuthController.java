package com.r2s.ApiWebReview.controller;

import com.r2s.ApiWebReview.common.response.ApiResponse;
import com.r2s.ApiWebReview.dto.AuthResponse;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.dto.UserResponse;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.mapper.UserMapper;
import com.r2s.ApiWebReview.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        UserResponse dto = userMapper.toResponseWithRole(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(dto, "Đăng ký thành công!"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse res = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.success(res, "Đăng nhập thành công!"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@CookieValue(value = "refreshToken", required = false) String token) {
        if (token == null) throw new BadRequestException("Không tìm thấy refresh token");
        AuthResponse res = authService.refreshAccessToken(token);
        return ResponseEntity.ok(ApiResponse.success(res, "Làm mới token thành công"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(value = "refreshToken", required = false) String token,
                                                    HttpServletResponse response) {
        if (token == null) throw new BadRequestException("Không có refresh token để đăng xuất");
        authService.logout(token, response);
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công!"));
    }

}