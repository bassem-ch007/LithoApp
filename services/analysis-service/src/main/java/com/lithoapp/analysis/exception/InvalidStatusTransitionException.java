package com.lithoapp.analysis.exception;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(AnalysisStatus from, AnalysisStatus to) {
        super("Cannot transition analysis status from " + from + " to " + to);
    }
}
