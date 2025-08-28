package com.medical.medicationservice.service;

import com.medical.medicationservice.dto.InteractionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DrugKnowledgeService {

    private final StringRedisTemplate redis;

    // Placeholder monographs & side effects.
    // In real life: query proper DB/API; cache with Redis.
    public Map<String, Object> monograph(String genericDrug) {
        String key = "mono:" + genericDrug.toLowerCase();
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            // keep simple: just signal cache hit (not parsing JSON here)
        }
        Map<String, Object> mono = new HashMap<>();
        switch (genericDrug.toLowerCase()) {
            case "amoxicillin" -> {
                mono.put("benefits", List.of("Effective against common respiratory pathogens"));
                mono.put("side_common", List.of("Nausea", "Diarrhea", "Rash"));
                mono.put("side_serious", List.of("Anaphylaxis", "C. difficile diarrhea"));
                mono.put("contra", List.of("Penicillin allergy"));
            }
            case "doxycycline" -> {
                mono.put("benefits", List.of("Covers atypicals and penicillin allergies"));
                mono.put("side_common", List.of("Photosensitivity", "GI upset"));
                mono.put("side_serious", List.of("Esophagitis", "Hepatotoxicity (rare)"));
                mono.put("contra", List.of("Pregnancy", "Children <8 yrs"));
            }
            default -> {
                mono.put("benefits", List.of("General efficacy (placeholder)"));
                mono.put("side_common", List.of("Headache", "Nausea"));
                mono.put("side_serious", List.of("Rare severe reaction"));
                mono.put("contra", List.of());
            }
        }
        // Cache a marker to avoid rework (TTL optional)
        redis.opsForValue().set(key, "1");
        return mono;
    }

    public List<InteractionDto> interactions(List<String> meds, String candidate) {
        // Placeholder interactions. Real life: use a DDI DB.
        List<InteractionDto> out = new ArrayList<>();
        for (String m : meds == null ? List.<String>of() : meds) {
            if (m.toLowerCase().contains("warfarin") && candidate.equalsIgnoreCase("amoxicillin")) {
                out.add(InteractionDto.builder()
                        .with("Warfarin")
                        .severity("Moderate")
                        .mechanism("Reduced gut flora → ↑INR")
                        .action("Monitor INR; adjust dose if needed")
                        .build());
            }
        }
        return out;
    }

    public String adjustedDose(String drug, String renal, String hepatic) {
        // Super-simplified dose adjustment placeholders
        if ("amoxicillin".equalsIgnoreCase(drug) && renal != null && renal.toLowerCase().contains("ckd")) {
            return "500 mg PO q12h (renal adjust)";
        }
        return "";
    }
}
