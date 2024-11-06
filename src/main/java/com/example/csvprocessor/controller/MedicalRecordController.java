package com.example.csvprocessor.controller;

import com.example.csvprocessor.dto.MedicalRecordDTO;
import com.example.csvprocessor.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        service.uploadCSV(file);
        return ResponseEntity.ok("Data uploaded successfully");
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordDTO>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }

    @GetMapping("/{code}")
    public ResponseEntity<MedicalRecordDTO> getRecordByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getRecordByCode(code));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllRecords() {
        service.deleteAllRecords();
        return ResponseEntity.ok("All records deleted successfully");
    }
}