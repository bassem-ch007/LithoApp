package com.lithoapp.analysis.domain.enums;

/**
 * The three distinct PDF sub-results that make up a METABOLIC analysis.
 * A METABOLIC request can only be completed once all three are present.
 */
public enum MetabolicDocumentType {
    BLOOD_TEST,
    MORNING_URINE,
    H24_URINE
}
