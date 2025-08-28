package com.medical.diagnosisservice.controller;

import com.medical.diagnosisservice.dto.AnalyzeRequest;
import com.medical.diagnosisservice.dto.DiagnosisResponse;
import com.medical.diagnosisservice.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping("/analyze")
    public ResponseEntity<DiagnosisResponse> analyzeText(@RequestBody @Valid AnalyzeRequest req) {
        return ResponseEntity.ok(diagnosisService.analyzeText(req.getText(), req.getPreferredLang()));
    }

    @PostMapping(value = "/analyze-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiagnosisResponse> analyzeAudio(
            @RequestPart("audio") MultipartFile audio,
            @RequestPart(value = "lang", required = false) String lang) {
        return ResponseEntity.ok(diagnosisService.analyzeAudio(audio, lang));
    }

    @PostMapping(value = "/analyze-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiagnosisResponse> analyzeImage(
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "lang", required = false) String lang) {
        return ResponseEntity.ok(diagnosisService.analyzeImage(image, lang));
    }
}
