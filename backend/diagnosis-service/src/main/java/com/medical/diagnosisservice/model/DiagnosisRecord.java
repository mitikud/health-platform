package com.medical.diagnosisservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisRecord {
    @Id
    private String id;

    private String userId;         // (optional) set later when JWT is wired
    private String inputType;      // "text" | "audio" | "image"
    private String inputRef;       // S3 key or inline text
    private String language;       // requested language
    private String result;         // diagnosis text
    private double confidence;
    private Instant createdAt;
}
