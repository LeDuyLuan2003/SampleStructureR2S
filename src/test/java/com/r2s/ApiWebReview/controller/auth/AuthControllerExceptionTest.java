//package com.r2s.ApiWebReview.controller.auth;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
//import com.r2s.ApiWebReview.controller.AuthController;
//import com.r2s.ApiWebReview.dto.LoginRequest;
//import com.r2s.ApiWebReview.dto.RegisterRequest;
//import com.r2s.ApiWebReview.entity.VerificationToken;
//import com.r2s.ApiWebReview.exception.type.BadRequestException;
//import com.r2s.ApiWebReview.exception.type.UnauthorizedException;
//import com.r2s.ApiWebReview.service.AuthService;
//import com.r2s.ApiWebReview.service.OtpService;
//import com.r2s.ApiWebReview.service.VerificationTokenService;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc(addFilters = false)
//@WebMvcTest(AuthController.class)
//@Import(AuthControllerTestConfig.class)
//class AuthControllerExceptionTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//    @Autowired private AuthService authService;
//    @Autowired private VerificationTokenService verificationTokenService;
//    @Autowired private OtpService otpService;
//
//    @Test
//    void refresh_ShouldReturn400_WhenMissingRefreshToken() throws Exception {
//        mockMvc.perform(post("/api/auth/refresh"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Không tìm thấy refresh token"));
//    }
//
//    @Test
//    void refresh_ShouldReturn401_WhenExpiredToken() throws Exception {
//        when(authService.refreshAccessToken(any()))
//                .thenThrow(new UnauthorizedException("Refresh token đã hết hạn"));
//
//        mockMvc.perform(post("/api/auth/refresh")
//                        .cookie(new Cookie("refreshToken", "expired-token")))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("Refresh token đã hết hạn"));
//    }
//
//    @Test
//    void logout_ShouldReturn400_WhenMissingRefreshToken() throws Exception {
//        mockMvc.perform(post("/api/auth/logout"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Không có refresh token để đăng xuất"));
//    }
//
//    @Test
//    void verifyEmail_ShouldReturn400_WhenTokenInvalid() throws Exception {
//        when(verificationTokenService.findByToken("invalid")).thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/auth/verify?token=invalid"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Token không hợp lệ"));
//    }
//
//    @Test
//    void verifyEmail_ShouldReturn400_WhenTokenExpired() throws Exception {
//        VerificationToken token = new VerificationToken();
//        token.setExpiryDate(Instant.now().minusSeconds(60)); // expired
//        when(verificationTokenService.findByToken("expired")).thenReturn(Optional.of(token));
//
//        mockMvc.perform(post("/api/auth/verify?token=expired"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Token đã hết hạn!"));
//    }
//
//    @Test
//    void verifyOtp_ShouldReturn500_WhenOtpServiceFailsUnexpectedly() throws Exception {
//        // giả lập lỗi runtime bất ngờ
//        doThrow(new RuntimeException("Unexpected")).when(otpService).verifyOtp("123456");
//
//        mockMvc.perform(post("/api/auth/verify-otp?otp=123456"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Lỗi hệ thống, vui lòng thử lại sau."));
//    }
//}
