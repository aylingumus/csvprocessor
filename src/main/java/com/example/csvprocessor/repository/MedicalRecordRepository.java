package com.example.csvprocessor.repository;

import com.example.csvprocessor.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {
}