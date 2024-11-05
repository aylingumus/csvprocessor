package com.example.csvprocessor.exception;

public class CSVProcessingException extends RuntimeException {
    public CSVProcessingException(String message) {
        super(message);
    }
}