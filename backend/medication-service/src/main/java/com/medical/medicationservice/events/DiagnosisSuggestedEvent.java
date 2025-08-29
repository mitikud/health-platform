package com.medical.medicationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisSuggestedEvent {
    private String requestId;        // diagnosis record id
    private String userId;           // optional
    private String diagnosis;        // text
    private double confidence;
    private String language;
    private long timestamp;
}
