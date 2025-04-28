package com.r2s.ApiWebReview;

import com.r2s.ApiWebReview.common.enums.RoleEnum;
import com.r2s.ApiWebReview.common.event.UserRegisteredEvent;
import com.r2s.ApiWebReview.common.util.JwtUtil;
import com.r2s.ApiWebReview.dto.LoginRequest;
import com.r2s.ApiWebReview.dto.RegisterRequest;
import com.r2s.ApiWebReview.entity.RefreshToken;
import com.r2s.ApiWebReview.entity.Role;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.exception.type.ResourceNotFoundException;
import com.r2s.ApiWebReview.repository.RoleRepository;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.RefreshTokenService;
import com.r2s.ApiWebReview.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    // ---------- Test Case TC_REGISTER_01 ----------
    @Test
    void register_shouldSaveUser_whenEmailIsNew() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setFullname("New User");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // Act
        User result = authService.register(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(roleRepository).findByName(RoleEnum.USER);
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    // ---------- Test Case TC_REGISTER_02 ----------
    @Test
    void register_shouldThrowBadRequestException_whenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act + Assert
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(request));

        assertEquals("Email đã được đăng ký.", exception.getMessage());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ---------- Test Case TC_REGISTER_03 ----------
    @Test
    void register_shouldThrowResourceNotFoundException_whenRoleUserNotFound() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setFullname("New User");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.register(request));

        assertEquals("Không tìm thấy role USER", exception.getMessage());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(roleRepository).findByName(RoleEnum.USER);
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ---------- Test Case TC_LOGIN_01 ----------
    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("validuser@example.com");
        request.setPassword("correctPassword");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-sample");

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("access-token-sample");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // Act
        var authResponse = authService.login(request, response);

        // Assert
        assertNotNull(authResponse);
        assertEquals("access-token-sample", authResponse.getAccessToken());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil).generateToken(user);
        verify(refreshTokenService).createRefreshToken(user);
    }

    // ---------- Test Case TC_LOGIN_02 ----------
    @Test
    void login_shouldThrowUnauthorizedException_whenPasswordIsWrong() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("validuser@example.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act + Assert
        assertThrows(com.r2s.ApiWebReview.exception.type.UnauthorizedException.class,
                () -> authService.login(request, response));

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil, never()).generateToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    // ---------- Test Case TC_LOGIN_03 ----------
    @Test
    void login_shouldThrowUnauthorizedException_whenEmailNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("anyPassword");

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(com.r2s.ApiWebReview.exception.type.UnauthorizedException.class,
                () -> authService.login(request, response));

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    // ---------- Test Case TC_REFRESH_01 ----------
    @Test
    void refreshAccessToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        // Arrange
        String refreshTokenValue = "valid-refresh-token";

        User user = new User();
        user.setEmail("user@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.generateToken(user)).thenReturn("new-access-token");

        // Act
        var authResponse = authService.refreshAccessToken(refreshTokenValue);

        // Assert
        assertNotNull(authResponse);
        assertEquals("new-access-token", authResponse.getAccessToken());
        verify(refreshTokenService).findByToken(refreshTokenValue);
        verify(refreshTokenService).verifyExpiration(refreshToken);
        verify(jwtUtil).generateToken(user);
    }

    // ---------- Test Case TC_REFRESH_02 ----------
    @Test
    void refreshAccessToken_shouldThrowUnauthorizedException_whenRefreshTokenNotFound() {
        // Arrange
        String refreshTokenValue = "invalid-refresh-token";

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(com.r2s.ApiWebReview.exception.type.UnauthorizedException.class,
                () -> authService.refreshAccessToken(refreshTokenValue));

        verify(refreshTokenService).findByToken(refreshTokenValue);
        verify(refreshTokenService, never()).verifyExpiration(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    // ---------- Test Case TC_REFRESH_03 ----------
    @Test
    void refreshAccessToken_shouldThrowUnauthorizedException_whenRefreshTokenExpired() {
        // Arrange
        String refreshTokenValue = "expired-refresh-token";

        User user = new User();
        user.setEmail("user@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken))
                .thenThrow(new com.r2s.ApiWebReview.exception.type.UnauthorizedException("Refresh token đã hết hạn, cần đăng nhập lại."));

        // Act + Assert
        assertThrows(com.r2s.ApiWebReview.exception.type.UnauthorizedException.class,
                () -> authService.refreshAccessToken(refreshTokenValue));

        verify(refreshTokenService).findByToken(refreshTokenValue);
        verify(refreshTokenService).verifyExpiration(refreshToken);
        verify(jwtUtil, never()).generateToken(any());
    }

    // ---------- Test Case TC_LOGOUT_01 ----------
    @Test
    void logout_shouldDeleteRefreshTokenAndClearCookie_whenTokenIsValid() {
        // Arrange
        String refreshTokenValue = "valid-refresh-token";

        User user = new User();
        user.setEmail("user@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));

        // Act
        authService.logout(refreshTokenValue, response);

        // Assert
        verify(refreshTokenService).findByToken(refreshTokenValue);
        verify(refreshTokenService).deleteByUser(user);
        verify(response).addHeader(eq("Set-Cookie"), contains("refreshToken=;"));
    }

    // ---------- Test Case TC_LOGOUT_02 ----------
    @Test
    void logout_shouldClearCookie_whenTokenNotFound() {
        // Arrange
        String refreshTokenValue = "invalid-refresh-token";

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

        // Act
        authService.logout(refreshTokenValue, response);

        // Assert
        verify(refreshTokenService).findByToken(refreshTokenValue);
        verify(refreshTokenService, never()).deleteByUser(any());
        verify(response).addHeader(eq("Set-Cookie"), contains("refreshToken=;"));
    }


}
