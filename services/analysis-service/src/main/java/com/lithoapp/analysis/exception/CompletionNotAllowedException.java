package com.lithoapp.analysis.exception;

public class CompletionNotAllowedException extends RuntimeException {

    public CompletionNotAllowedException(String reason) {
        super("Cannot complete analysis request: " + reason);
    }
}
