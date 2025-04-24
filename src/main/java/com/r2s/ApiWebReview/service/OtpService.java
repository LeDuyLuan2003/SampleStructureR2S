package com.r2s.ApiWebReview.service;

public interface OtpService {
    String generateAndSendOtp(String email);
    void verifyOtp(String otp);
}
