package com.r2s.ApiWebReview.config;


import com.r2s.ApiWebReview.service.MailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestMailConfig {

    @Bean
    public MailService mailService() {
        return mock(MailService.class);
    }
}

