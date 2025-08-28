package com.medical.notificationservice.controller;

import com.medical.notificationservice.dto.NotificationRequest;
import com.medical.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody @Valid NotificationRequest req) {
        notificationService.send(req);
        return ResponseEntity.ok("OK");
    }
}

