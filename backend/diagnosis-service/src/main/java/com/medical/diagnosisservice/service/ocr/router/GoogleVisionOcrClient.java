package com.medical.diagnosisservice.service.ocr.router;

import com.medical.diagnosisservice.service.ocr.OcrClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component("cloudOcr")  // @Profile("prod") for GoogleVisionOcrClient
public class GoogleVisionOcrClient implements OcrClient {
    @Override
    public String extractText(InputStream imageStream, String filename) throws Exception {
        return "";
    } }
