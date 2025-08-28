package com.medical.medicationservice.controller;

import com.medical.medicationservice.dto.MedicationRequest;
import com.medical.medicationservice.dto.MedicationResponse;
import com.medical.medicationservice.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping("/recommend")
    public ResponseEntity<MedicationResponse> recommend(@RequestBody @Valid MedicationRequest request) {
        return ResponseEntity.ok(medicationService.recommend(request));
    }
}