package com.medical.diagnosisservice.service.LLM;

import com.medical.diagnosisservice.dto.DiagnosisPayload;

public interface LlmClient {
//    String suggestDiagnosis(String symptoms, String locale) throws Exception;
    DiagnosisPayload suggestDiagnosis(String symptoms, String locale) throws Exception;
}

