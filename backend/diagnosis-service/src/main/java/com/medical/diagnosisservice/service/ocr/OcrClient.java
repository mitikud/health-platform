package com.medical.diagnosisservice.service.ocr;

import java.io.InputStream;

public interface OcrClient {
    String extractText(InputStream imageStream, String filename) throws Exception;
}
