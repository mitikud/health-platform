package com.medical.medicationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TranslationClient {

    @Value("${translation.base-url:http://translation-service:8085}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String translate(String text, String targetLang) {
        try {
            String url = baseUrl + "/api/translate";
            Map<String, String> payload = Map.of("text", text, "targetLang", targetLang);
            ResponseEntity<String> res = restTemplate.postForEntity(url, payload, String.class);
            return (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) ? res.getBody() : text;
        } catch (Exception e) {
            return text; // fallback to original
        }
    }
}
