package com.medical.medicationservice.kafka.listner;

import com.medical.medicationservice.dto.MedicationRequest;
import com.medical.medicationservice.dto.MedicationResponse;
import com.medical.medicationservice.events.DiagnosisSuggestedEvent;
import com.medical.medicationservice.events.MedicationPlanCreatedEvent;
import com.medical.medicationservice.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisEventsHandler {

    private final MedicationService medicationService; // your existing service
    private final KafkaTemplate<String, MedicationPlanCreatedEvent> template;

    @Value("${topics.planCreated:meds.plan.created}")
    private String planTopic;

    @KafkaListener(topics = "${topics.diagnosisSuggested:diagnosis.suggested}",
            containerFactory = "diagnosisListenerFactory")
    public void onDiagnosis(DiagnosisSuggestedEvent evt) {
        // Build a minimal MedicationRequest from the diagnosis
        var req = new MedicationRequest();
        req.setFreeTextDiagnosis(evt.getDiagnosis());
        req.setLanguage(evt.getLanguage());
        req.setDiagnosisCodes(java.util.List.of()); // optional

        MedicationResponse res = medicationService.recommend(req);

        var planEvt = MedicationPlanCreatedEvent.builder()
                .planId(res.getPlanId())
                .diagnosis(evt.getDiagnosis())
                .language(evt.getLanguage())
                .timestamp(System.currentTimeMillis())
                .build();

        template.send(planTopic, res.getPlanId(), planEvt);
    }
}
