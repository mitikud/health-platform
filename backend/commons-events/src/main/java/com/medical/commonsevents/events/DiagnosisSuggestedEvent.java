package com.medical.commonsevents.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisSuggestedEvent {
    private String requestId;
    private String userId;
    private String diagnosis;
    private double confidence;
    private String language;
    private long timestamp;
}
