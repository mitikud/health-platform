package com.medical.translationservice.service.TTS.impl;

import com.medical.translationservice.service.TTS.TtsClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public class GoogleTtsClient implements TtsClient {

    @Override
    public byte[] synthesize(String text, String lang) throws Exception {
        try (var client = com.google.cloud.texttospeech.v1.TextToSpeechClient.create()) {
            var input = com.google.cloud.texttospeech.v1.SynthesisInput.newBuilder()
                    .setText(text).build();
            var voice = com.google.cloud.texttospeech.v1.VoiceSelectionParams.newBuilder()
                    .setLanguageCode(lang) // "en-US", "am-ET", "ti-ER" (check supported voices)
                    .build();
            var audioConfig = com.google.cloud.texttospeech.v1.AudioConfig.newBuilder()
                    .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.MP3)
                    .build();
            var resp = client.synthesizeSpeech(input, voice, audioConfig);
            return resp.getAudioContent().toByteArray();
        }
    }
}

