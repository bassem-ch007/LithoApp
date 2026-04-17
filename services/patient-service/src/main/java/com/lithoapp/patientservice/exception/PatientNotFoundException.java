package com.lithoapp.patientservice.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id);
    }

    public PatientNotFoundException(String field, String value) {
        super("Patient not found with " + field + ": " + value);
    }
}
