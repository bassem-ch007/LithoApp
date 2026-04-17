package com.lithoapp.patientservice.exception;

public class DuplicateIdentifierException extends RuntimeException {

    public DuplicateIdentifierException(String field, String value) {
        super("A patient with " + field + " '" + value + "' already exists");
    }
}
