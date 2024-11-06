package com.example.csvprocessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String codeListCode;

    @Id
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String displayValue;

    private String longDescription;

    @Column(nullable = false)
    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer sortingPriority;
}
