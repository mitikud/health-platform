package com.medical.translationservice.service.TTS;

public interface TtsClient {
    byte[] synthesize(String text, String langCode) throws Exception; // return MP3/OGG
}

