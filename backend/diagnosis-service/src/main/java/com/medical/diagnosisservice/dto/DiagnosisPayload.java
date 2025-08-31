package com.medical.diagnosisservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisPayload {
    private List<String> possible;          // e.g., ["Migraine", "Tension-type headache"]
    private List<String> recommendations;   // e.g., ["Hydration", "Rest", "See clinician if persistent"]
}
