package com.medical.medicationservice.controller;

import com.medical.medicationservice.dto.MedicationRequest;
import com.medical.medicationservice.dto.MedicationResponse;
import com.medical.medicationservice.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping("/recommend")
    public ResponseEntity<MedicationResponse> recommend(@RequestBody @Valid MedicationRequest request) {
        return ResponseEntity.ok(medicationService.recommend(request));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('PHARMACIST')")
    @PostMapping("/{planId}/approve")
    public ResponseEntity<Void> approve(@PathVariable String planId) { /* ... */ return ResponseEntity.ok().build(); }

//    @PreAuthorize("hasRole('DOCTOR')")
//    @PostMapping("/{planId}/revise")
//    public ResponseEntity<Void> revise(@PathVariable String planId, @RequestBody ReviseDto dto) { /* ... */ return ResponseEntity.ok().build(); }
}