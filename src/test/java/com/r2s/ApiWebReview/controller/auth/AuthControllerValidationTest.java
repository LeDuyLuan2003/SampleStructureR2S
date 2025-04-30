package com.r2s.ApiWebReview.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r2s.ApiWebReview.config.AuthControllerTestConfig;
import com.r2s.ApiWebReview.controller.AuthController;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
@Import(AuthControllerTestConfig.class)
class AuthControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL = "/api/auth/register";

    // Helper method
    private void performAndExpect(RegisterRequest request, int expectedStatus) throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(expectedStatus));
    }

    @Nested
    @DisplayName("Fullname Validation")
    class FullnameValidation {
        @Test
        @DisplayName("Fullname is null")
        void shouldReturn400_WhenFullnameIsNull() throws Exception {
            performAndExpect(new RegisterRequest(null, "test@example.com", "Password123"), 400);
        }

        @Test
        @DisplayName("Fullname is blank")
        void shouldReturn400_WhenFullnameIsBlank() throws Exception {
            performAndExpect(new RegisterRequest("", "test@example.com", "Password123"), 400);
        }

        @Test
        @DisplayName("Fullname too short")
        void shouldReturn400_WhenFullnameTooShort() throws Exception {
            performAndExpect(new RegisterRequest("A", "test@example.com", "Password123"), 400);
        }

        @Test
        @DisplayName("Fullname too long")
        void shouldReturn400_WhenFullnameTooLong() throws Exception {
            performAndExpect(new RegisterRequest("A".repeat(101), "test@example.com", "Password123"), 400);
        }

        @Test
        @DisplayName("Fullname valid")
        void shouldReturn201_WhenFullnameValid() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "test@example.com", "Password123"), 201);
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {
        @Test
        @DisplayName("Email is null")
        void shouldReturn400_WhenEmailIsNull() throws Exception {
            performAndExpect(new RegisterRequest("Test User", null, "Password123"), 400);
        }

        @Test
        @DisplayName("Email is blank")
        void shouldReturn400_WhenEmailIsBlank() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "", "Password123"), 400);
        }

        @Test
        @DisplayName("Email invalid format")
        void shouldReturn400_WhenEmailInvalid() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "invalid-email", "Password123"), 400);
        }

        @Test
        @DisplayName("Email valid")
        void shouldReturn201_WhenEmailValid() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "valid@example.com", "Password123"), 201);
        }
    }

    @Nested
    @DisplayName("Password Validation")
    class PasswordValidation {
        @Test
        @DisplayName("Password is null")
        void shouldReturn400_WhenPasswordIsNull() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "test@example.com", null), 400);
        }

        @Test
        @DisplayName("Password is blank")
        void shouldReturn400_WhenPasswordIsBlank() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "test@example.com", ""), 400);
        }

        @Test
        @DisplayName("Password too short")
        void shouldReturn400_WhenPasswordTooShort() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "test@example.com", "1234567"), 400);
        }

        @Test
        @DisplayName("Password valid")
        void shouldReturn201_WhenPasswordValid() throws Exception {
            performAndExpect(new RegisterRequest("Test User", "test@example.com", "Password123"), 201);
        }
    }
}
