package com.medical.diagnosisservice.service.ocr;

import com.medical.diagnosisservice.dto.DiagnosisResponse;

import java.io.InputStream;

public interface OcrClient {
    String extractText(InputStream imageStream, String filename) throws Exception;
//DiagnosisResponse extractText(InputStream imageStream, String filename) throws Exception;
}
