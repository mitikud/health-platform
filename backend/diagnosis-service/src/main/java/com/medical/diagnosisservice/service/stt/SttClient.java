package com.medical.diagnosisservice.service.stt;


import java.io.InputStream;

public interface SttClient {
    String transcribe(InputStream audioStream, String filename, String locale) throws Exception;
}

