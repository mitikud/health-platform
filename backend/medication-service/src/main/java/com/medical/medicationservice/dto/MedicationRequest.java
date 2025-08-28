package com.medical.medicationservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class MedicationRequest {
    @NotEmpty
    private List<String> diagnosisCodes; // ICD-10/SNOMED if available
    private String freeTextDiagnosis;

    private PatientProfile patientProfile;
    private String language; // en, am, ti

    @Data
    public static class PatientProfile {
        private Integer age;
        private String sex;          // M|F|Other
        private Double weightKg;
        private Boolean pregnant;
        private Boolean breastfeeding;
        private String renal;        // normal|ckd1..5
        private String hepatic;      // normal|child-pugh-a/b/c
        private List<String> allergies;
        private List<String> conditions;       // e.g., "hypertension"
        private List<String> activeMedications; // free-text for MVP
    }
}
