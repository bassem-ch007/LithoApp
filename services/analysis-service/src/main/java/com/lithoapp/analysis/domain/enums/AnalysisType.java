package com.lithoapp.analysis.domain.enums;

/**
 * The kind of analysis being requested.
 * METABOLIC = 3 PDF sub-results (blood, morning urine, 24h urine).
 * STONE     = structured morphology + spectrophotometry + classification.
 */
public enum AnalysisType {
    METABOLIC,
    STONE
}
