package com.mohamed.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleEmail {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;

    @Async
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
