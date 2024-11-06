package com.example.csvprocessor.service;

import com.example.csvprocessor.dto.MedicalRecordDTO;
import com.example.csvprocessor.entity.MedicalRecord;
import com.example.csvprocessor.exception.CSVProcessingException;
import com.example.csvprocessor.exception.RecordNotFoundException;
import com.example.csvprocessor.repository.MedicalRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedicalRecordService service;

    @Test
    void getAllRecords_ShouldReturnAllRecordsDTOs() {
        List<MedicalRecord> expectedRecords = Arrays.asList(
                new MedicalRecord("ZIB", "ZIB001", "271636001", "Test", "", LocalDate.of(2019, 1, 1), null, 1),
                new MedicalRecord("ZIB", "ZIB001", "61086009", "Test 2", "", LocalDate.of(2019, 1, 1), null, 2)
        );
        when(repository.findAll()).thenReturn(expectedRecords);

        when(modelMapper.map(eq(expectedRecords.get(0)), eq(MedicalRecordDTO.class)))
                .thenReturn(new MedicalRecordDTO("ZIB", "ZIB001", "271636001", "Test", "", LocalDate.of(2019, 1, 1), null, 1));
        when(modelMapper.map(eq(expectedRecords.get(1)), eq(MedicalRecordDTO.class)))
                .thenReturn(new MedicalRecordDTO("ZIB", "ZIB001", "61086009", "Test 2", "", LocalDate.of(2019, 1, 1), null, 2));

        List<MedicalRecordDTO> actualRecords = service.getAllRecords();

        assertEquals(2, actualRecords.size());
        assertEquals("271636001", actualRecords.get(0).getCode());
        assertEquals("61086009", actualRecords.get(1).getCode());
        verify(repository).findAll();
    }

    @Test
    void getRecordByCode_ShouldReturnRecordDTO() {
        String code = "271636001";
        MedicalRecord expectedRecord = new MedicalRecord("ZIB", "ZIB001", code, "Test", "", LocalDate.of(2019, 1, 1), null, 1);
        when(repository.findById(code)).thenReturn(Optional.of(expectedRecord));

        when(modelMapper.map(eq(expectedRecord), eq(MedicalRecordDTO.class)))
                .thenReturn(new MedicalRecordDTO("ZIB", "ZIB001", "271636001", "Test", "", LocalDate.of(2019, 1, 1), null, 1));

        MedicalRecordDTO actualRecord = service.getRecordByCode(code);

        assertEquals(code, actualRecord.getCode());
        verify(repository).findById(code);
    }

    @Test
    void uploadCSV_ShouldProcessAndSaveRecords() {
        String csvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "ZIB,ZIB001,271636001,Test,,01-01-2019,,1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        service.uploadCSV(file);

        verify(repository).saveAll(any());
    }

    @Test
    void getRecordByCode_WhenRecordNotFound_ShouldThrowRecordNotFoundException() {
        String nonExistentCode = "non-existent-code";
        when(repository.findById(nonExistentCode)).thenReturn(java.util.Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> service.getRecordByCode(nonExistentCode));
    }

    @Test
    void uploadCSV_WhenFailedToProcessFile_ShouldThrowException() {
        String csvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "ZIB,ZIB001,271636001,Test,,01-01-2019,,1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(repository.saveAll(any())).thenThrow(new RuntimeException("Failed to save records"));

        assertThrows(RuntimeException.class, () -> service.uploadCSV(file));
    }

    @Test
    void uploadCSV_WhenAllRecordsAlreadyExist_ShouldNotSaveAnyRecord() {
        String csvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "ZIB,ZIB001,271636001,Test,,01-01-2019,,1\n" +
                "ZIB,ZIB001,61086009,Test 2,,01-01-2019,,2";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(repository.existsById("271636001")).thenReturn(true);
        when(repository.existsById("61086009")).thenReturn(true);

        CSVProcessingException exception = assertThrows(CSVProcessingException.class, () -> service.uploadCSV(file));
        assertEquals("Failed to process CSV file: All data are already uploaded", exception.getMessage());

        verify(repository, never()).saveAll(any());
    }

    @Test
    void deleteAllRecords_ShouldDeleteAllRecords() {
        service.deleteAllRecords();

        verify(repository).deleteAll();
    }
}