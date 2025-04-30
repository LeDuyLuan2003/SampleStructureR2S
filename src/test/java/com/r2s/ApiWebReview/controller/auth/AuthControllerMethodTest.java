package com.r2s.ApiWebReview.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r2s.ApiWebReview.common.response.ApiResponse;
import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
import com.r2s.ApiWebReview.controller.AuthController;
import com.r2s.ApiWebReview.dto.*;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.entity.VerificationToken;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.service.OtpService;
import com.r2s.ApiWebReview.service.VerificationTokenService;
import com.r2s.ApiWebReview.mapper.UserMapper;
import com.r2s.ApiWebReview.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthControllerTestConfig.class)
class AuthControllerMethodTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private AuthService authService;
    @Autowired private UserMapper userMapper;
    @Autowired private VerificationTokenService verificationTokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private OtpService otpService;

    @Test
    void login_ShouldReturn200_WhenValidCredentials() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("user@example.com");
        login.setPassword("Password123");

        when(authService.login(any(), any())).thenReturn(new AuthResponse("token", 3600));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("token"))
                .andExpect(jsonPath("$.message").value("Đăng nhập thành công!"));
    }

    @Test
    void refresh_ShouldReturn200_WhenValidRefreshToken() throws Exception {
        when(authService.refreshAccessToken(any()))
                .thenReturn(new AuthResponse("new-token", 3600));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "valid-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-token"))
                .andExpect(jsonPath("$.message").value("Làm mới token thành công"));
    }

    @Test
    void refresh_ShouldReturn400_WhenMissingToken() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Không tìm thấy refresh token"));
    }

    @Test
    void logout_ShouldReturn200_AndClearCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("refreshToken", "token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đăng xuất thành công!"));
    }

    @Test
    void logout_ShouldReturn400_WhenMissingCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Không có refresh token để đăng xuất"));
    }

    @Test
    void verifyEmail_ShouldReturn200_WhenValidToken() throws Exception {
        VerificationToken token = new VerificationToken();
        User user = new User();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        when(verificationTokenService.findByToken("abc123")).thenReturn(Optional.of(token));

        mockMvc.perform(post("/api/auth/verify?token=abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Xác thực email thành công!"));
    }

    @Test
    void verifyEmail_ShouldReturn400_WhenExpiredToken() throws Exception {
        VerificationToken token = new VerificationToken();
        token.setExpiryDate(Instant.now().minusSeconds(10));
        token.setUser(new User());

        when(verificationTokenService.findByToken("expired")).thenReturn(Optional.of(token));

        mockMvc.perform(post("/api/auth/verify?token=expired"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token đã hết hạn!"));
    }

    @Test
    void verifyEmail_ShouldReturn400_WhenTokenInvalid() throws Exception {
        when(verificationTokenService.findByToken("invalid")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/verify?token=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token không hợp lệ"));
    }

    @Test
    void verifyOtp_ShouldReturn200_WhenOtpIsValid() throws Exception {
        mockMvc.perform(post("/api/auth/verify-otp?otp=123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("Xác thực bằng OTP thành công!"));
    }
}
