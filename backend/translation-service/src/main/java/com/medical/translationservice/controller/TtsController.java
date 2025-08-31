package com.medical.translationservice.controller;

import com.medical.translationservice.service.TTS.TtsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TtsController {
    private final TtsClient tts;

    @PostMapping(value="/speak", produces="audio/mpeg")
    public ResponseEntity<byte[]> speak(@RequestParam String text, @RequestParam String lang) {
        try {
            byte[] mp3 = tts.synthesize(text, lang);
            return ResponseEntity.ok()
                    .header("Content-Disposition","inline; filename=tts.mp3")
                    .body(mp3);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

