package com.r2s.ApiWebReview.service.impl;

import com.r2s.ApiWebReview.entity.OtpVerification;
import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.exception.type.BadRequestException;
import com.r2s.ApiWebReview.repository.OtpVerificationRepository;
import com.r2s.ApiWebReview.repository.UserRepository;
import com.r2s.ApiWebReview.service.MailService;
import com.r2s.ApiWebReview.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpVerificationRepository otpRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MailService mailService;

    @Override
    public String generateAndSendOtp(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new BadRequestException("Email không tồn tại!"));

        otpRepo.deleteByUser(user); // xóa OTP cũ nếu có

        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setUser(user);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(Instant.now().plusSeconds(300));
        otpRepo.save(otpEntity);

        mailService.sendOtpEmail(user.getEmail(), otp);
        return otp;
    }
    @Transactional
    @Override
    public void verifyOtp(String otp) {
        OtpVerification otpEntity = otpRepo.findByOtp(otp)
                .orElseThrow(() -> new BadRequestException("OTP không hợp lệ!"));

        if (otpEntity.getExpiryTime().isBefore(Instant.now())) {
            throw new BadRequestException("OTP đã hết hạn!");
        }

        User user = otpEntity.getUser();
        user.setEnabled(true);
        userRepo.save(user);
        otpRepo.deleteByUser(user);
    }
}
