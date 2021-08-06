package com.example.pizza.exception;

public class InvalidCsvException extends RuntimeException {
    public InvalidCsvException(String message) {
        super(message);
    }
}
