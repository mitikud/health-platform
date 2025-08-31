package com.medical.diagnosisservice.service.ocr.impl;

import com.medical.diagnosisservice.service.ocr.OcrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

//@Profile("prod")
@Component("localOcr")  // @Profile("offline") for TessOcrClient
public class TessOcrClient implements OcrClient {
    @Value("${ocr.tesseract.datapath:/tessdata}") private String dataPath;
    @Value("${ocr.tesseract.lang:eng}") private String lang; // add amh, tir traineddata as needed

    @Override
    public String extractText(InputStream imageStream, String filename) throws Exception {
        var temp = java.nio.file.Files.createTempFile("rx_", "_" + filename);
        java.nio.file.Files.write(temp, imageStream.readAllBytes());
        net.sourceforge.tess4j.Tesseract t = new net.sourceforge.tess4j.Tesseract();
        t.setDatapath(dataPath);
        t.setLanguage(lang); // for Amharic: "amh", Tigrinya: "tir" (you must install .traineddata)
        try {
            return t.doOCR(temp.toFile());
        } finally {
            java.nio.file.Files.deleteIfExists(temp);
        }
    }
}
