package com.medical.diagnosisservice.service.stt.router;

import com.medical.diagnosisservice.service.stt.SttClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Profile({"prod"})
@Component("cloudStt")
public class WhisperSttClient implements SttClient {

    @Override
    public String transcribe(InputStream audioStream, String filename, String locale) throws Exception {
        return "";
    }
}


