package com.medical.diagnosisservice.service.LLM.impl;

import com.medical.diagnosisservice.dto.DiagnosisPayload;
import com.medical.diagnosisservice.service.LLM.LlmClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("localLlm") @Profile({"offline","default"})
public class RuleBasedLlmClient implements LlmClient {
    public DiagnosisPayload suggestDiagnosis(String symptoms, String locale) {
        return DiagnosisPayload.builder()
                .possible(java.util.List.of("General checkup recommended"))
                .recommendations(java.util.List.of("Hydration","Rest"))
                .build();
    }
}
