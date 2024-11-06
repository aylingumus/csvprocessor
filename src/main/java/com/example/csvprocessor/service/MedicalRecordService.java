package com.example.csvprocessor.service;

import com.example.csvprocessor.dto.MedicalRecordDTO;
import com.example.csvprocessor.entity.MedicalRecord;
import com.example.csvprocessor.exception.CSVProcessingException;
import com.example.csvprocessor.exception.RecordNotFoundException;
import com.example.csvprocessor.repository.MedicalRecordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository repository;
    private final ModelMapper modelMapper;

    public void uploadCSV(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> allLines;
            List<MedicalRecord> records;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .build()) {
                allLines = csvReader.readAll();
            }

            Set<String> csvCodes = allLines.stream()
                    .map(line -> line[2])
                    .collect(Collectors.toSet());

            boolean allExist = csvCodes.stream().allMatch(repository::existsById);

            if (allExist) {
                throw new CSVProcessingException("All data are already uploaded");
            }

            records = allLines.stream()
                    .map(line -> new MedicalRecord(
                            line[0], // source
                            line[1], // codeListCode
                            line[2], // code
                            line[3], // displayValue
                            line[4], // longDescription
                            LocalDate.parse(line[5], formatter), // fromDate
                            line[6].isEmpty() ? null : LocalDate.parse(line[6]), // toDate
                            line[7].isEmpty() ? null : Integer.parseInt(line[7]) // sortingPriority
                    ))
                    .collect(Collectors.toList());

            repository.saveAll(records);
        } catch (Exception e) {
            throw new CSVProcessingException("Failed to process CSV file: " + e.getMessage());
        }
    }

    public List<MedicalRecordDTO> getAllRecords() {
        List<MedicalRecord> records = repository.findAll();
        return records.stream()
                .map(record -> modelMapper.map(record, MedicalRecordDTO.class))
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO getRecordByCode(String code) {
        MedicalRecord record = repository.findById(code)
                .orElseThrow(() -> new RecordNotFoundException("Record not found with code: " + code));
        return modelMapper.map(record, MedicalRecordDTO.class);
    }

    public void deleteAllRecords() {
        repository.deleteAll();
    }
}