package com.medical.translationservice.service;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    public String translate(String text, String lang) {
        // TODO: plug real provider; for now, echo with a simple tag
        return switch (lang.toLowerCase()) {
            case "am" -> "[AM] " + text;
            case "ti" -> "[TI] " + text;
            default -> text;
        };
    }
}

