package com.medical.medicationservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationItemDto {
    private String drug;          // Generic (Brand)
    private String dose;          // e.g., 500 mg PO q8h x 7d
    private String rationale;
    private List<String> benefits;
    private List<String> sideEffectsCommon;
    private List<String> sideEffectsSerious;
    private List<String> contraindications;
    private java.util.List<InteractionDto> interactions;
    private java.util.List<String> saferAlternatives;
    private MonitoringDto monitoring;
    private double riskScore;     // 0..1
}
