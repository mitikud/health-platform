package com.medical.diagnosisservice.service;

import com.medical.diagnosisservice.common.events.DiagnosisSuggestedEvent;
import com.medical.diagnosisservice.dto.DiagnosisResponse;
import com.medical.diagnosisservice.model.DiagnosisRecord;
import com.medical.diagnosisservice.repository.DiagnosisRepository;
import com.medical.diagnosisservice.service.ocr.OcrClient;
import com.medical.diagnosisservice.service.storage.ObjectStorageService;
import com.medical.diagnosisservice.service.stt.SttClient;
import com.medical.diagnosisservice.util.Lang;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final KafkaTemplate<String, DiagnosisSuggestedEvent> template;

    @Value("${topics.diagnosisSuggested:diagnosis.suggested}")
    private String topic;

    private final DiagnosisRepository repository;
    private final OcrClient ocrClient;
    private final SttClient sttClient;
    private final ObjectStorageService storage;

    //kafka
    private void emitSuggested(String id, String diagnosis, double conf, String lang) {
        var evt = DiagnosisSuggestedEvent.builder()
                .requestId(id).diagnosis(diagnosis).confidence(conf)
                .language(lang).timestamp(System.currentTimeMillis())
                .build();
        template.send(topic, id, evt);
    }
    // Placeholder AI diagnosis logic
    private String inferDiagnosis(String normalizedText) {
        // TODO: call LLM/NLP model; return best-guess
        if (normalizedText.toLowerCase().contains("headache")) return "Migraine (suggested)";
        if (normalizedText.toLowerCase().contains("cough")) return "Upper respiratory infection (suggested)";
        return "General checkup recommended (suggested)";
    }

    public DiagnosisResponse analyzeText(String text, String lang) {
        String normalized = text.trim();
        String result = inferDiagnosis(normalized);
        double confidence = 0.65; // placeholder

        DiagnosisRecord rec = DiagnosisRecord.builder()
                .inputType("text")
                .inputRef(normalized)
                .language(Lang.normalize(lang))
                .result(result)
                .confidence(confidence)
                .createdAt(Instant.now())
                .build();
        repository.save(rec);

        return DiagnosisResponse.builder()
                .diagnosis(result)
                .confidence(confidence)
                .language(rec.getLanguage())
                .source("text")
                .requestId(rec.getId())
                .build();
    }

    public DiagnosisResponse analyzeAudio(MultipartFile audio, String lang) {
        try (InputStream in = audio.getInputStream()) {
            String key = storage.upload("audio", in, audio.getSize(), audio.getContentType());
            // STT
            String transcript = sttClient.transcribe(audio.getInputStream(), audio.getOriginalFilename(), Lang.locale(lang));
            String result = inferDiagnosis(transcript);
            double confidence = 0.6; // placeholder

            DiagnosisRecord rec = DiagnosisRecord.builder()
                    .inputType("audio")
                    .inputRef(key)
                    .language(Lang.normalize(lang))
                    .result(result)
                    .confidence(confidence)
                    .createdAt(Instant.now())
                    .build();
            repository.save(rec);

            return DiagnosisResponse.builder()
                    .diagnosis(result)
                    .confidence(confidence)
                    .language(rec.getLanguage())
                    .source("audio->stt")
                    .requestId(rec.getId())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Audio analyze failed", e);
        }
    }

    public DiagnosisResponse analyzeImage(MultipartFile image, String lang) {
        try (InputStream in = image.getInputStream()) {
            String key = storage.upload("images", in, image.getSize(), image.getContentType());
            // OCR
            String ocrText = ocrClient.extractText(image.getInputStream(), image.getOriginalFilename());
            String result = inferDiagnosis(ocrText);
            double confidence = 0.55; // placeholder

            DiagnosisRecord rec = DiagnosisRecord.builder()
                    .inputType("image")
                    .inputRef(key)
                    .language(Lang.normalize(lang))
                    .result(result)
                    .confidence(confidence)
                    .createdAt(Instant.now())
                    .build();
            repository.save(rec);

            return DiagnosisResponse.builder()
                    .diagnosis(result)
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
