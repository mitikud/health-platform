package com.medical.translationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TranslationRequest {
    @NotBlank
    private String text;
    @NotBlank private String targetLang; // en | am | ti
}

