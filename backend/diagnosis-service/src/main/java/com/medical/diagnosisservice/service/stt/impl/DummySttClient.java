package com.medical.diagnosisservice.service.stt.impl;

import com.medical.diagnosisservice.service.stt.SttClient;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DummySttClient implements SttClient {
    @Override
    public String transcribe(InputStream audioStream, String filename, String locale) {
        // TODO replace with real STT (Whisper/Google STT/Azure)
        return "Dummy STT transcript (" + locale + ") for " + filename;
    }
}
