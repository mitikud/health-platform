package com.medical.medicationservice.model;

import com.medical.medicationservice.dto.RecommendationItemDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("medication_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationPlan {
    @Id
    private String id;

    private String userId; // optional for now

    private List<String> diagnosisCodes;
    private String freeTextDiagnosis;

    private String language;
    private List<RecommendationItemDto> items;

    private String status;    // DRAFT|APPROVED|REJECTED (DDSS later)
    private Instant createdAt;
}
