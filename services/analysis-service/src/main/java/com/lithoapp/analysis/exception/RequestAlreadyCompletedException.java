package com.lithoapp.analysis.exception;

public class RequestAlreadyCompletedException extends RuntimeException {

    public RequestAlreadyCompletedException(Long id) {
        super("Analysis request " + id + " is already COMPLETED and cannot be modified.");
    }
}
