package com.example.csvprocessor.controller;

import com.example.csvprocessor.dto.MedicalRecordDTO;
import com.example.csvprocessor.exception.CSVProcessingException;
import com.example.csvprocessor.exception.RecordNotFoundException;
import com.example.csvprocessor.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService service;

    @Test
    void getAllRecords_ShouldReturnListOfRecordDTOs() throws Exception {
        List<MedicalRecordDTO> expectedRecords = Arrays.asList(
                new MedicalRecordDTO("ZIB", "ZIB001", "271636001", "Test", "", LocalDate.of(2019, 1, 1), null, 1),
                new MedicalRecordDTO("ZIB", "ZIB001", "61086009", "Test 2", "", LocalDate.of(2019, 1, 1), null, 2)
        );
        when(service.getAllRecords()).thenReturn(expectedRecords);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/medical-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("271636001"))
                .andExpect(jsonPath("$[1].code").value("61086009"));
    }

    @Test
    void getRecordByCode_ShouldReturnRecordDTO() throws Exception {
        String code = "271636001";
        MedicalRecordDTO expectedRecord = new MedicalRecordDTO("ZIB", "ZIB001", code, "Test", "", LocalDate.of(2019, 1, 1), null, 1);
        when(service.getRecordByCode(code)).thenReturn(expectedRecord);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/medical-records/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code));
    }

    @Test
    void uploadFile_ShouldUploadAndProcessCSV() throws Exception {
        String csvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "ZIB,ZIB001,271636001,Test,,01-01-2019,,1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );
        doNothing().when(service).uploadCSV(file);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/medical-records/upload")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void getRecordByCode_WhenRecordNotFound_ShouldReturn404() throws Exception {
        String nonExistentCode = "non-existent-code";
        when(service.getRecordByCode(nonExistentCode))
                .thenThrow(new RecordNotFoundException("Record not found with code: " + nonExistentCode));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/medical-records/{code}", nonExistentCode))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadFile_WhenFailedToProcessFile_ShouldReturn500() throws Exception {
        String csvContent = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "ZIB,ZIB001,271636001,Test,,01-01-2019,,1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        doThrow(new RuntimeException("Failed to save records")).when(service).uploadCSV(file);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/medical-records/upload")
                        .file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadFile_WhenAllRecordsAlreadyExist_ShouldReturnMeaningfulMessage() throws Exception {
        String csvContent = """
                source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority
                ZIB,ZIB001,271636001,Test,,01-01-2019,,1
                ZIB,ZIB001,61086009,Test 2,,01-01-2019,,2""";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes()
        );

        doThrow(new CSVProcessingException("All data are already uploaded")).when(service).uploadCSV(file);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/medical-records/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("All data are already uploaded"));
    }

    @Test
    void deleteAllRecords_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(service).deleteAllRecords();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/medical-records"))
                .andExpect(status().isOk())
                .andExpect(content().string("All records deleted successfully"));
    }
}