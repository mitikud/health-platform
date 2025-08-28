package com.medical.medicationservice.repository;

import com.medical.medicationservice.model.MedicationPlan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MedicationPlanRepository extends MongoRepository<MedicationPlan, String> {
}
