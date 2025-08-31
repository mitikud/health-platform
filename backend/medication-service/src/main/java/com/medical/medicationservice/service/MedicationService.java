package com.medical.medicationservice.service;

import com.medical.medicationservice.dto.*;
import com.medical.medicationservice.model.MedicationPlan;
import com.medical.medicationservice.repository.MedicationPlanRepository;
import com.medical.medicationservice.util.Lang;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationPlanRepository repository;
    private final DrugKnowledgeService knowledge;
    private final TranslationClient translator;

    public MedicationResponse recommend(MedicationRequest req) {
        String lang = Lang.normalize(req.getLanguage());
        var pf = req.getPatientProfile();
        List<String> activeMeds = pf != null ? Optional.ofNullable(pf.getActiveMedications()).orElse(List.of()) : List.of();
        List<String> allergies = pf != null ? Optional.ofNullable(pf.getAllergies()).orElse(List.of()) : List.of();

        // Candidate generation (toy logic, replace with AI later)
        List<String> candidates = new ArrayList<>();
        String dx = (req.getFreeTextDiagnosis() == null ? "" : req.getFreeTextDiagnosis().toLowerCase());
        if (dx.contains("penicillin allergy")) {
            candidates.add("Doxycycline");
        } else if (dx.contains("sinus") || dx.contains("respiratory") || dx.contains("cough")) {
            candidates.add("Amoxicillin");
            candidates.add("Doxycycline");
        } else {
            candidates.add("Paracetamol");
        }

        // Score/filter candidates with simple rules
        List<RecommendationItemDto> items = new ArrayList<>();
        for (String drug : candidates) {
            String generic = drug;
            Map<String, Object> mono = knowledge.monograph(generic);

            // Contraindications / allergy filter (very simplified)
            if (allergies.stream().anyMatch(a -> a.toLowerCase().contains("penicillin"))
                    && generic.equalsIgnoreCase("Amoxicillin")) {
                // prefer safer alternative, but still show as blocked
                continue;
            }

            String baseDose = switch (generic.toLowerCase()) {
                case "amoxicillin" -> "500 mg PO q8h x 7d";
                case "doxycycline" -> "100 mg PO q12h x 7-10d";
                default -> "As directed";
            };

            // Adjust dose (renal/hepatic)
            String adjust = knowledge.adjustedDose(generic, pf != null ? pf.getRenal() : null, pf != null ? pf.getHepatic() : null);
            String dose = adjust.isBlank() ? baseDose : adjust;

            // Interactions
            List<InteractionDto> interactions = knowledge.interactions(activeMeds, generic);

            // Safer alternatives (toy)
            List<String> safer = new ArrayList<>();
            if (generic.equalsIgnoreCase("Amoxicillin") && allergies.stream().anyMatch(a -> a.toLowerCase().contains("penicillin"))) {
                safer.add("Doxycycline");
            }

            @SuppressWarnings("unchecked")
            List<String> benefits = (List<String>) mono.getOrDefault("benefits", List.of("Efficacy (placeholder)"));
            @SuppressWarnings("unchecked")
            List<String> sideCommon = (List<String>) mono.getOrDefault("side_common", List.of("Nausea"));
            @SuppressWarnings("unchecked")
            List<String> sideSerious = (List<String>) mono.getOrDefault("side_serious", List.of());
            @SuppressWarnings("unchecked")
            List<String> contra = (List<String>) mono.getOrDefault("contra", List.of());

            double riskScore = interactions.stream().anyMatch(i -> "Major".equalsIgnoreCase(i.getSeverity())) ? 0.9 :
                    interactions.stream().anyMatch(i -> "Moderate".equalsIgnoreCase(i.getSeverity())) ? 0.4 : 0.2;

            RecommendationItemDto item = RecommendationItemDto.builder()
                    .drug(generic)
                    .dose(dose)
                    .rationale(localize("Empiric therapy aligned with common pathogens.", lang))
                    .benefits(translateAll(benefits, lang))
                    .sideEffectsCommon(translateAll(sideCommon, lang))
                    .sideEffectsSerious(translateAll(sideSerious, lang))
                    .contraindications(translateAll(contra, lang))
                    .interactions(interactions)       // keep drug names canonical
                    .saferAlternatives(safer)         // kept canonical
                    .monitoring(MonitoringDto.builder()
                            .labs(List.of())              // fill with knowledge base later
                            .vitals(List.of())
                            .symptoms(translateAll(List.of("Rash", "Severe diarrhea", "Breathing difficulty"), lang))
                            .build())
                    .riskScore(riskScore)
                    .build();

            items.add(item);
        }

        MedicationPlan plan = MedicationPlan.builder()
                .diagnosisCodes(req.getDiagnosisCodes())
                .freeTextDiagnosis(req.getFreeTextDiagnosis())
                .language(lang)
                .items(items)
                .status("DRAFT") // DDSS gate later
                .createdAt(Instant.now())
                .build();
        repository.save(plan);

        return MedicationResponse.builder()
                .planId(plan.getId())
                .language(lang)
                .recommendations(items)
                .disclaimer(localize("Educational only. Requires clinician review.", lang))
                .build();
    }

    public MedicationPlan getPlan(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Medication plan not found: " + id));
    }
    private List<String> translateAll(List<String> lines, String lang) {
        List<String> out = new ArrayList<>();
        for (String s : lines) out.add(localize(s, lang));
        return out;
    }

    private String localize(String text, String lang) {
        if ("en".equals(lang)) return text;
        return translator.translate(text, lang);
    }
}
