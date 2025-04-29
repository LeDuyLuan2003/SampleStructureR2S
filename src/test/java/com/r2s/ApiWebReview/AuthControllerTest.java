package com.r2s.ApiWebReview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
import com.r2s.ApiWebReview.controller.AuthController;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.dto.UserResponse;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.mapper.UserMapper;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.OtpService;
import com.r2s.ApiWebReview.service.VerificationTokenService;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    // ---- TEST CASES /api/auth/register ----

    @Test
    void register_ShouldReturn201_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullname("Test User");
        request.setPassword("Password123");

        User mockUser = new User();
        when(authService.register(any())).thenReturn(mockUser);
        when(userMapper.toResponseWithRole(mockUser)).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đăng ký thành công!"));
    }

    @Test
    void register_ShouldReturn400_WhenEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("exist@example.com");
        request.setFullname("Exist User");
        request.setPassword("Password123");

        when(authService.register(any()))
                .thenThrow(new BadRequestException("Email đã tồn tại"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email đã tồn tại"));
    }

    @Test
    void register_ShouldReturn400_WhenFullnameIsNull() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenFullnameTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullname("A");
        request.setEmail("test@example.com");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenEmailIsNull() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullname("Test User");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenEmailInvalidFormat() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullname("Test User");
        request.setEmail("invalid-email");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenPasswordIsNull() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullname("Test User");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullname("Test User");
        request.setEmail("test@example.com");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenAllFieldsEmpty() throws Exception {
        RegisterRequest request = new RegisterRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenEmailCaseInsensitiveDuplicate() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("Test@Example.com");
        request.setFullname("Test User");
        request.setPassword("Password123");

        when(authService.register(any()))
                .thenThrow(new BadRequestException("Email đã tồn tại"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn201_WhenPasswordContainsWhitespace() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullname("Test User");
        request.setPassword("Password 123");

        User mockUser = new User();
        when(authService.register(any())).thenReturn(mockUser);
        when(userMapper.toResponseWithRole(mockUser)).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_ShouldReturn201_WhenPasswordContainsSpecialCharacters() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullname("Test User");
        request.setPassword("P@ssword123!");

        User mockUser = new User();
        when(authService.register(any())).thenReturn(mockUser);
        when(userMapper.toResponseWithRole(mockUser)).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_ShouldReturn500_WhenServiceThrowsRuntimeException() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFullname("Test User");
        request.setPassword("Password123");

        when(authService.register(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
