package com.r2s.ApiWebReview.config;

import com.r2s.ApiWebReview.common.util.JwtUtil;
import com.r2s.ApiWebReview.service.AuthService;
import com.r2s.ApiWebReview.mapper.UserMapper;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.OtpService;
import com.r2s.ApiWebReview.service.VerificationTokenService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthControllerTestConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }

    @Bean
    public VerificationTokenService verificationTokenService() {
        return Mockito.mock(VerificationTokenService.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public OtpService otpService() {
        return Mockito.mock(OtpService.class);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }
}
