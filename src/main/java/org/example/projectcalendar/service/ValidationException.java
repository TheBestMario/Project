package org.example.projectcalendar.service;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
} 