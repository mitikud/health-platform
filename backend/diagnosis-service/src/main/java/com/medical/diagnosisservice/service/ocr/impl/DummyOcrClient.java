package com.medical.diagnosisservice.service.ocr.impl;

import com.medical.diagnosisservice.service.ocr.OcrClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DummyOcrClient implements OcrClient {
    @Override
    public String extractText(InputStream imageStream, String filename) {
        // TODO replace with real OCR (Google Vision, AWS Textract, Tesseract)
        return "Dummy OCR extracted text from " + filename;
    }
}
