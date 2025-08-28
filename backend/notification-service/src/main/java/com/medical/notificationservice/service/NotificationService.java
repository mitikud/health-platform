package com.medical.notificationservice.service;

import com.medical.notificationservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final JavaMailSender mailSender;

    public void send(NotificationRequest req) {
        switch (req.getChannel().toLowerCase()) {
            case "email" -> sendEmail(req.getToEmail(), req.getSubject(), req.getMessage());
            case "sms"   -> sendSms(req.getToPhone(), req.getMessage());
            default      -> throw new IllegalArgumentException("Unknown channel: " + req.getChannel());
        }
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            var msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.warn("Email send failed: {}", e.getMessage());
        }
    }

    private void sendSms(String to, String text) {
        // TODO: integrate Twilio; for now, just log
        log.info("SMS to {} -> {}", to, text);
    }
}

