package com.r2s.ApiWebReview.service.impl;

import com.r2s.ApiWebReview.entity.User;
import com.r2s.ApiWebReview.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${CLIENT_URL:http://localhost:8080}")
    private String clientUrl;

    @Override
    public void sendVerificationEmail(User user, String token) {
        String subject = "Xác thực tài khoản của bạn";
        String verifyUrl = clientUrl + "/api/auth/verify?token=" + token;
        String content = "<p>Xin chào " + user.getFullname() + ",</p>"
                + "<p>Vui lòng nhấn vào liên kết dưới đây để xác thực tài khoản của bạn:</p>"
                + "<p><a href='" + verifyUrl + "'>Xác thực tài khoản</a></p>"
                + "<p>Liên kết có hiệu lực trong 15 phút.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email xác thực", e);
        }
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        String subject = "Mã xác thực OTP";
        String content = "<p>Xin chào,</p>"
                + "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã có hiệu lực trong 5 phút.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi mã OTP", e);
        }
    }

}
