package com.medical.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotBlank private String channel; // email | sms
    private String toEmail;
    private String toPhone;
    @NotBlank
    private String subject;
    @NotBlank private String message;
}

