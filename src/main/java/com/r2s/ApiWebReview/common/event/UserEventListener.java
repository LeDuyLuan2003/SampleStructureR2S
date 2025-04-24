package com.r2s.ApiWebReview.common.event;

import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.service.MailService;
import com.r2s.ApiWebReview.service.OtpService;
import com.r2s.ApiWebReview.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private OtpService otpService;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        // Gửi email xác thực bằng link
        String token = verificationTokenService.createToken(user).getToken();
        mailService.sendVerificationEmail(user, token);

        // Gửi OTP
        otpService.generateAndSendOtp(user.getEmail());
    }
}