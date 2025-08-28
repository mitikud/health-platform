package com.medical.reportservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ReportRequest {
    @NotBlank
    private String userId;
    @NotBlank private String language; // en | am | ti
    // Optional fields; in real system, you might fetch details by plan/diagnosis IDs
    private String diagnosis;
    private List<String> medications;
    private String notes;
}

