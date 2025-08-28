package com.medical.medicationservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringDto {
    private List<String> labs;     // e.g., INR, creatinine
    private List<String> vitals;   // BP, HR
    private List<String> symptoms; // things to watch for
}
