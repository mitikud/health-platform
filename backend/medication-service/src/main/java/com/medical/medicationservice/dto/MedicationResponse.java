package com.medical.medicationservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MedicationResponse {
    private String planId;
    private String language;
    private List<RecommendationItemDto> recommendations;
    private String disclaimer;
}
