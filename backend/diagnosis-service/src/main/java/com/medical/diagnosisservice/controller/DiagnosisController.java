package com.medical.diagnosisservice.controller;

import com.medical.diagnosisservice.dto.AnalyzeRequest;
import com.medical.diagnosisservice.dto.DiagnosisResponse;
import com.medical.diagnosisservice.service.DiagnosisService;
import com.medical.diagnosisservice.util.ProcessingMode;
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

//    @PostMapping("/analyze")
//    public ResponseEntity<DiagnosisResponse> analyzeText(@RequestBody @Valid AnalyzeRequest req) {
//        return ResponseEntity.ok(diagnosisService.analyzeText(req.getText(), req.getPreferredLang()));
//    }
@PostMapping("/analyze")
public ResponseEntity<DiagnosisResponse> analyzeText(
        @RequestBody @Valid AnalyzeRequest req,
        @RequestHeader(value="X-Processing-Mode", required=false) String modeHeader,
        @RequestParam(value="mode", required=false) String modeParam) {
    var mode = parseMode(modeHeader, modeParam);
    return ResponseEntity.ok(diagnosisService.analyzeText(req.getText(), req.getPreferredLang(), mode));
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
    private ProcessingMode parseMode(String h, String p) {
        String v = (h != null && !h.isBlank()) ? h : (p != null ? p : "AUTO");
        return switch (v.toUpperCase()) {
            case "LOCAL" -> ProcessingMode.LOCAL;
            case "CLOUD" -> ProcessingMode.CLOUD;
            default -> ProcessingMode.AUTO;
        };
    }
}
