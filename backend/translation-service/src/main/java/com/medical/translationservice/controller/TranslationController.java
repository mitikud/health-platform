package com.medical.translationservice.controller;

import com.medical.translationservice.dto.TranslationRequest;
import com.medical.translationservice.service.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<String> translate(@RequestBody @Valid TranslationRequest req) {
        return ResponseEntity.ok(translationService.translate(req.getText(), req.getTargetLang()));
    }
}

