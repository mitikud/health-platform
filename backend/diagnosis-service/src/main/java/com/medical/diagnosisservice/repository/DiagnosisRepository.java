package com.medical.diagnosisservice.repository;

import com.medical.diagnosisservice.model.DiagnosisRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiagnosisRepository extends MongoRepository<DiagnosisRecord, String> {
}
