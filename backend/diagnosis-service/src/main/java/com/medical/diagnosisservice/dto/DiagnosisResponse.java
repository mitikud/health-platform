package com.medical.diagnosisservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagnosisResponse {
    private DiagnosisPayload analysis;
    private String diagnosis;
    private double confidence; // 0..1
    private String language;   // en/am/ti
    private String source;     // "text" | "audio->stt" | "image->ocr"
    private String requestId;
}
