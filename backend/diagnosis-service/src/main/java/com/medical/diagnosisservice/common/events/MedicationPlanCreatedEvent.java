package com.medical.diagnosisservice.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationPlanCreatedEvent {
    private String planId;
    private String userId;
    private String diagnosis;
    private String language;
    private long timestamp;
}
