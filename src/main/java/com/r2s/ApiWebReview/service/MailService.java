package com.r2s.ApiWebReview.service;


import com.r2s.ApiWebReview.entity.User;

public interface MailService {
    void sendVerificationEmail(User user, String token);
    void sendOtpEmail(String to, String otp);
}
