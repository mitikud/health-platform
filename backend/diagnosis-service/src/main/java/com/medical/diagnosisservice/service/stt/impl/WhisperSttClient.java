package com.medical.diagnosisservice.service.stt.impl;

import com.medical.diagnosisservice.service.stt.SttClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class WhisperSttClient implements SttClient {

    @Value("${ai.openai.apiKey}") private String apiKey;
    @Value("${ai.openai.whisper.model:gpt-4o-mini-transcribe}") private String model;

    @Override
    public String transcribe(InputStream audio, String filename, String locale) throws Exception {
        var client = new okhttp3.OkHttpClient();
        var body = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)
                .addFormDataPart("model", model)
                .addFormDataPart("file", filename,
                        okhttp3.RequestBody.create(
                                audio.readAllBytes(),
                                okhttp3.MediaType.parse("audio/*")))
                .addFormDataPart("language", locale) // e.g., en, am, ti (providers may expect ISO)
                .build();

        var req = new okhttp3.Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (var resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new RuntimeException("Whisper STT failed: "+resp.code());
            var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp.body().string());
            return json.path("text").asText();
        }
    }
}

