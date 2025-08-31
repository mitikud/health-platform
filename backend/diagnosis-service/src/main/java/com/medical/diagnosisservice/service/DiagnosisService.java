package com.medical.diagnosisservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.diagnosisservice.common.events.DiagnosisSuggestedEvent;
import com.medical.diagnosisservice.dto.DiagnosisPayload;
import com.medical.diagnosisservice.dto.DiagnosisResponse;
import com.medical.diagnosisservice.model.DiagnosisRecord;
import com.medical.diagnosisservice.repository.DiagnosisRepository;
import com.medical.diagnosisservice.service.LLM.LlmClient;
import com.medical.diagnosisservice.service.LLM.router.LlmRouter;
import com.medical.diagnosisservice.service.audio.AudioPreprocessor;
import com.medical.diagnosisservice.service.ocr.OcrClient;
import com.medical.diagnosisservice.service.ocr.router.OcrRouter;
import com.medical.diagnosisservice.service.storage.ObjectStorageService;
import com.medical.diagnosisservice.service.stt.SttClient;
import com.medical.diagnosisservice.service.stt.router.SttRouter;
import com.medical.diagnosisservice.util.Lang;
import com.medical.diagnosisservice.util.ProcessingMode;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;

@Service
//@RequiredArgsConstructor
public class DiagnosisService {
    private final LlmClient llmClient;
    private final AudioPreprocessor audioPreprocessor;
    private final KafkaTemplate<String, DiagnosisSuggestedEvent> template;

    @Value("${topics.diagnosisSuggested:diagnosis.suggested}")
    private String topic;

    private final DiagnosisRepository repository;

    @Qualifier("googleVisionOcrClient")
    private final OcrClient ocrClient;

    private final SttClient sttClient;
    private final ObjectStorageService storage;
    private final ObjectMapper mapper = new ObjectMapper();
    public DiagnosisService(
            LlmClient llmClient, AudioPreprocessor audioPreprocessor,
            KafkaTemplate<String, DiagnosisSuggestedEvent> template,
            DiagnosisRepository repository,
            SttClient sttClient,
            ObjectStorageService storage,
            @Qualifier("googleVisionOcrClient") OcrClient ocrClient // <- choose one
    ) {
        this.llmClient = llmClient;
        this.audioPreprocessor = audioPreprocessor;
        this.template = template;
        this.repository = repository;
        this.sttClient = sttClient;
        this.storage = storage;
        this.ocrClient = ocrClient;
    }
    // utility
    private DiagnosisPayload fallbackPayload() {
        return DiagnosisPayload.builder()
                .possible(java.util.List.of("General checkup recommended"))
                .recommendations(java.util.List.of("Hydration","Rest","See clinician if symptoms persist >48h"))
                .build();
    }
    //kafka
//    private void emitSuggested(String id, String diagnosis, double conf, String lang) {
//        var evt = DiagnosisSuggestedEvent.builder()
//                .requestId(id).diagnosis(diagnosis).confidence(conf)
//                .language(lang).timestamp(System.currentTimeMillis())
//                .build();
//        template.send(topic, id, evt);
//    }

    private void emitSuggested(String id, DiagnosisPayload payload, double conf, String lang) {
        String top = (payload.getPossible() != null && !payload.getPossible().isEmpty())
                ? payload.getPossible().get(0)
                : "Unknown";
        var evt = DiagnosisSuggestedEvent.builder()
                .requestId(id)
                .diagnosis(top)
                .confidence(conf)
                .language(lang)
                .timestamp(System.currentTimeMillis())
                .build();
        template.send(topic, id, evt);
    }
    // Placeholder AI diagnosis logic
//    private String inferDiagnosis(String normalizedText, String lang) {
//        // TODO: call LLM/NLP model; return best-guess
//        try {
////            String json = llmClient.suggestDiagnosis(normalizedText, Lang.locale(lang));
//            String json = llmClient.suggestDiagnosis(normalizedText, Lang.locale(lang)).toString();
//            // Optionally parse & normalize to your DTO, here we just return the JSON string
//            return json;
//        } catch (Exception e) {
//            // graceful fallback
//            return """
//        { "possible": ["General checkup recommended"],
//          "recommendations": ["Hydration", "Rest", "See a clinician if symptoms persist >48h"] }
//        """;
//        }
////        if (normalizedText.toLowerCase().contains("headache")) return "Migraine (suggested)";
////        if (normalizedText.toLowerCase().contains("cough")) return "Upper respiratory infection (suggested)";
////        return "General checkup recommended (suggested)";
//    }
    private DiagnosisPayload inferDiagnosis(String normalizedText, String lang) {
        try {
            return llmClient.suggestDiagnosis(normalizedText, Lang.locale(lang));
        } catch (Exception e) {
            return fallbackPayload();
        }
    }
    private String toJson(DiagnosisPayload p) {
        try { return mapper.valueToTree(p).toString(); }
        catch (Exception e) { return "{\"possible\":[\"General checkup recommended\"]}"; }
    }
//    public DiagnosisResponse analyzeText(String text, String lang) {
//        String normalized = text.trim();
//        String result = inferDiagnosis(normalized, lang);
//        double confidence = 0.6; // placeholder
//
//        DiagnosisRecord rec = DiagnosisRecord.builder()
//                .inputType("text")
////                .inputRef(normalized)
//                .inputRef(text)
//                .language(Lang.normalize(lang))
//                .result(result)
//                .confidence(confidence)
//                .createdAt(Instant.now())
//                .build();
//        repository.save(rec);
//
//        return DiagnosisResponse.builder()
//                .diagnosis(result)
//                .confidence(confidence)
//                .language(rec.getLanguage())
//                .source("text")
//                .requestId(rec.getId())
//                .build();
//    }
// text path
//public DiagnosisResponse analyzeText(String text, String lang) {
//    DiagnosisPayload payload;
//    try {
//        payload = llmClient.suggestDiagnosis(text.trim(), Lang.locale(lang));
//    } catch (Exception e) {
//        payload = fallbackPayload();
//    }
//    String resultJson = new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(payload).toString();
//    double confidence = 0.6;
//
//    DiagnosisRecord rec = DiagnosisRecord.builder()
//            .inputType("text")
//            .inputRef(text)
//            .language(Lang.normalize(lang))
//            .result(resultJson)
//            .confidence(confidence)
//            .createdAt(java.time.Instant.now())
//            .build();
//    repository.save(rec);
//
//    return DiagnosisResponse.builder()
//            .analysis(payload)
//            .diagnosis(String.join(", ", payload.getPossible()))
//            .confidence(confidence)
//            .language(rec.getLanguage())
//            .source("text")
//            .requestId(rec.getId())
//            .build();
//}
//    public DiagnosisResponse analyzeAudio(MultipartFile audio, String lang) {
//        try (InputStream in = audio.getInputStream()) {
//            String key = storage.upload("audio", in, audio.getSize(), audio.getContentType());
//            // STT
//            String transcript = sttClient.transcribe(audio.getInputStream(), audio.getOriginalFilename(), Lang.locale(lang));
//            String result = inferDiagnosis(transcript, lang);
//            double confidence = 0.6; // placeholder
//
//            DiagnosisRecord rec = DiagnosisRecord.builder()
//                    .inputType("audio")
//                    .inputRef(key)
//                    .language(Lang.normalize(lang))
//                    .result(result)
//                    .confidence(confidence)
//                    .createdAt(Instant.now())
//                    .build();
//            repository.save(rec);
//
//            return DiagnosisResponse.builder()
//                    .diagnosis(result)
//                    .confidence(confidence)
//                    .language(rec.getLanguage())
//                    .source("audio->stt")
//                    .requestId(rec.getId())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Audio analyze failed", e);
//        }
//    }
public DiagnosisResponse analyzeText(String text, String lang, ProcessingMode mode) {
    String normalized = text == null ? "" : text.trim();
    DiagnosisPayload payload = inferDiagnosis(normalized, lang);
    double confidence = 0.6;
    LlmRouter.setMode(mode);

    DiagnosisRecord rec = DiagnosisRecord.builder()
            .inputType("text")
            .inputRef(text)
            .language(Lang.normalize(lang))
            .result(toJson(payload))
            .confidence(confidence)
            .createdAt(Instant.now())
            .build();
    repository.save(rec);

    emitSuggested(rec.getId(), payload, confidence, rec.getLanguage());

    return DiagnosisResponse.builder()
            .analysis(payload)
            .diagnosis(String.join(", ", payload.getPossible()))
            .confidence(confidence)
            .language(rec.getLanguage())
            .source("text")
            .requestId(rec.getId())
            .build();
}


//    public DiagnosisResponse analyzeAudio(MultipartFile audio, String lang) {
//        try (InputStream in = audio.getInputStream()) {
//            String key = storage.upload("audio", in, audio.getSize(), audio.getContentType());
//            // STT
//            String transcript = sttClient.transcribe(audio.getInputStream(), audio.getOriginalFilename(), Lang.locale(lang));
//            String result = inferDiagnosis(transcript, lang);
//            double confidence = 0.6; // placeholder
//
//            DiagnosisRecord rec = DiagnosisRecord.builder()
//                    .inputType("audio")
//                    .inputRef(key)
//                    .language(Lang.normalize(lang))
//                    .result(result)
//                    .confidence(confidence)
//                    .createdAt(Instant.now())
//                    .build();
//            repository.save(rec);
//
//            return DiagnosisResponse.builder()
//                    .diagnosis(result)
//                    .confidence(confidence)
//                    .language(rec.getLanguage())
//                    .source("audio->stt")
//                    .requestId(rec.getId())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Audio analyze failed", e);
//        }
//    }

    // -------- AUDIO

//    public DiagnosisResponse analyzeAudio(MultipartFile audio, String lang) {
//        try {
//            byte[] bytes = audio.getBytes(); // read once
//            // upload
//            try (InputStream up = new ByteArrayInputStream(bytes)) {
//                storage.upload("audio", up, audio.getSize(), audio.getContentType());
//            }
//            // STT
//            String transcript;
//            try (InputStream sttIn = new ByteArrayInputStream(bytes)) {
//                transcript = sttClient.transcribe(sttIn, audio.getOriginalFilename(), Lang.locale(lang));
//            }
//
//            DiagnosisPayload payload = inferDiagnosis(transcript, lang);
//            double confidence = 0.6;
//
//            DiagnosisRecord rec = DiagnosisRecord.builder()
//                    .inputType("audio")
//                    .inputRef(audio.getOriginalFilename())
//                    .language(Lang.normalize(lang))
//                    .result(toJson(payload))
//                    .confidence(confidence)
//                    .createdAt(Instant.now())
//                    .build();
//            repository.save(rec);
//
//            emitSuggested(rec.getId(), payload, confidence, rec.getLanguage());
//
//            return DiagnosisResponse.builder()
//                    .analysis(payload)
//                    .diagnosis(String.join(", ", payload.getPossible()))
//                    .confidence(confidence)
//                    .language(rec.getLanguage())
//                    .source("audio->stt")
//                    .requestId(rec.getId())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Audio analyze failed", e);
//        }
//    }
public DiagnosisResponse analyzeAudio(MultipartFile audio, String lang, ProcessingMode mode) {
    File wav = null;
    try {
        SttRouter.setMode(mode);
        LlmRouter.setMode(mode);
        // 1) normalize
        wav = audioPreprocessor.toPcm16kWav(audio);

        // 2) upload original (optional) and/or normalized
        try (InputStream in = new FileInputStream(wav)) {
            String key = storage.upload("audio", in, wav.length(), "audio/wav");

            // 3) STT over normalized PCM
            String transcript;
            try (InputStream sttIn = new FileInputStream(wav)) {
                transcript = sttClient.transcribe(sttIn, "normalized.wav", Lang.locale(lang));
            }

            // 4) LLM diagnosis as before
            var payload = llmClient.suggestDiagnosis(transcript, Lang.locale(lang));
            var resultJson = new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(payload).toString();

            DiagnosisRecord rec = DiagnosisRecord.builder()
                    .inputType("audio")
                    .inputRef(key)
                    .language(Lang.normalize(lang))
                    .result(resultJson)
                    .confidence(0.6)
                    .createdAt(java.time.Instant.now())
                    .build();
            repository.save(rec);

            return DiagnosisResponse.builder()
                    .analysis(payload)
                    .diagnosis(String.join(", ", payload.getPossible()))
                    .confidence(0.6)
                    .language(rec.getLanguage())
                    .source("audio->stt")
                    .requestId(rec.getId())
                    .build();
        }
    } catch (Exception e) {
        throw new RuntimeException("Audio analyze failed", e);
    } finally {
        if (wav != null) wav.delete();
    }
}


//    public DiagnosisResponse analyzeImage(MultipartFile image, String lang) {
//        try (InputStream in = image.getInputStream()) {
//            String key = storage.upload("images", in, image.getSize(), image.getContentType());
//            // OCR
//            String ocrText = ocrClient.extractText(image.getInputStream(), image.getOriginalFilename());
////            DiagnosisResponse ocrText = ocrClient.extractText(image.getInputStream(), image.getOriginalFilename());
//            String result = inferDiagnosis(ocrText, lang);
////            DiagnosisResponse result = inferDiagnosis(ocrText, lang);
//            double confidence = 0.55; // placeholder
//
//            DiagnosisRecord rec = DiagnosisRecord.builder()
//                    .inputType("image")
//                    .inputRef(key)
//                    .language(Lang.normalize(lang))
//                    .result(result)
//                    .confidence(confidence)
//                    .createdAt(Instant.now())
//                    .build();
//            repository.save(rec);
//
//            return DiagnosisResponse.builder()
//                    .diagnosis(result)
//                    .confidence(confidence)
//                    .language(rec.getLanguage())
//                    .source("image->ocr")
//                    .requestId(rec.getId())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Image analyze failed", e);
//        }
//    }

    public DiagnosisResponse analyzeImage(MultipartFile image, String lang, ProcessingMode mode) {

        OcrRouter.setMode(mode);
        LlmRouter.setMode(mode);try {
            byte[] bytes = image.getBytes(); // read once
            // upload
            try (InputStream up = new ByteArrayInputStream(bytes)) {
                storage.upload("images", up, image.getSize(), image.getContentType());
            }
            // OCR
            String ocrText;
            try (InputStream ocrIn = new ByteArrayInputStream(bytes)) {
                ocrText = ocrClient.extractText(ocrIn, image.getOriginalFilename());
            }

            DiagnosisPayload payload = inferDiagnosis(ocrText, lang);
            double confidence = 0.55;

            DiagnosisRecord rec = DiagnosisRecord.builder()
                    .inputType("image")
                    .inputRef(image.getOriginalFilename())
                    .language(Lang.normalize(lang))
                    .result(toJson(payload))
                    .confidence(confidence)
                    .createdAt(Instant.now())
                    .build();
            repository.save(rec);

            emitSuggested(rec.getId(), payload, confidence, rec.getLanguage());

            return DiagnosisResponse.builder()
                    .analysis(payload)
                    .diagnosis(String.join(", ", payload.getPossible()))
                    .confidence(confidence)
                    .language(rec.getLanguage())
                    .source("image->ocr")
                    .requestId(rec.getId())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Image analyze failed", e);
        }
    }
}
