package com.lithoapp.patientservice.exception;

public class PatientDeletionNotAllowedException extends RuntimeException {

    public PatientDeletionNotAllowedException(Long patientId) {
        super("Patient " + patientId + " cannot be deleted because linked clinical episodes exist.");
    }
}
