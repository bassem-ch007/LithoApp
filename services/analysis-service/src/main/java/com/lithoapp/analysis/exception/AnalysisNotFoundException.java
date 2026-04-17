package com.lithoapp.analysis.exception;

public class AnalysisNotFoundException extends RuntimeException {

    public AnalysisNotFoundException(Long id) {
        super("Analysis request not found with id: " + id);
    }
}
