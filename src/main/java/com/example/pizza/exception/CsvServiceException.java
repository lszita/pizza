package com.example.pizza.exception;

public class CsvServiceException extends RuntimeException {
    public CsvServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
