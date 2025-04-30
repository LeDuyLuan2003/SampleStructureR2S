package com.r2s.ApiWebReview.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
import com.r2s.ApiWebReview.controller.AuthController;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.UserResponse;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTestConfig.class)
class AuthControllerSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private AuthService authService;
    @Autowired private UserMapper userMapper;

    @Test
    void login_ShouldSetHttpOnlyCookie_WhenSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authService.login(any(), any())).thenAnswer(invocation -> {
            var response = invocation.getArgument(1, jakarta.servlet.http.HttpServletResponse.class);
            Cookie cookie = new Cookie("refreshToken", "mock-refresh-token");
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return new com.r2s.ApiWebReview.dto.AuthResponse("access-token", 3600L);
        });

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void endpoints_ShouldIncludeSecurityHeaders() throws Exception {
        when(authService.register(any())).thenReturn(new User());
        when(userMapper.toResponseWithRole(any())).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"fullname\":\"Test\",\"password\":\"Password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("X-Content-Type-Options"));
    }

    @Test
    void shouldRejectCors_WhenOriginNotAllowed() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", "https://untrusted.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectStateChangingEndpoints_WhenCsrfMissing() throws Exception {
        String json = "{\"email\":\"test@example.com\",\"fullname\":\"Test\",\"password\":\"Password123\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
