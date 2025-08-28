package com.medical.medicationservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionDto {
    private String with;
    private String severity;     // Minor|Moderate|Major
    private String mechanism;    // CYP3A4 inhibitor, etc.
    private String action;       // Avoid/Monitor/Adjust dose
}
