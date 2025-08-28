package com.medical.diagnosisservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnalyzeRequest {
    @NotBlank
    private String text;          // Symptom description
    private String preferredLang; // "en" | "am" | "ti"
}
