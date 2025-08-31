package com.medical.diagnosisservice.service.LLM.router;

import com.medical.diagnosisservice.service.LLM.LlmClient;
import com.medical.diagnosisservice.util.ProcessingMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LlmRouter implements LlmClient {
    private final Optional<LlmClient> local;  // could be a small on-prem model or rule-based
    private final Optional<LlmClient> cloud;  // OpenAiLlmClient
    private static final ThreadLocal<ProcessingMode> MODE = new ThreadLocal<>();

    public static void setMode(ProcessingMode m){ MODE.set(m); }
    public static void clearMode(){ MODE.remove(); }

    public LlmRouter(@Qualifier("localLlm") Optional<LlmClient> local,
                     @Qualifier("cloudLlm") Optional<LlmClient> cloud) {
        this.local = local; this.cloud = cloud;
    }

    @Override
    public com.medical.diagnosisservice.dto.DiagnosisPayload suggestDiagnosis(String symptoms, String locale) throws Exception {
        var m = MODE.get();
        try {
            if (m == ProcessingMode.LOCAL && local.isPresent())
                return local.get().suggestDiagnosis(symptoms, locale);
            if (m == ProcessingMode.CLOUD && cloud.isPresent())
                return cloud.get().suggestDiagnosis(symptoms, locale);
            if (cloud.isPresent()) return cloud.get().suggestDiagnosis(symptoms, locale);
            if (local.isPresent()) return local.get().suggestDiagnosis(symptoms, locale);
            throw new IllegalStateException("No LLM provider available");
        } finally { clearMode(); }
    }
}

