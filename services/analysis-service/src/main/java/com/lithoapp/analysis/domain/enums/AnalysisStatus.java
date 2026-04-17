package com.lithoapp.analysis.domain.enums;

/**
 * Lifecycle status of an AnalysisRequest.
 *
 * Allowed transitions:
 *   CREATED      → IN_PROGRESS  (auto, on first result contribution)
 *   IN_PROGRESS  → COMPLETED    (explicit, validated)
 *
 * Once COMPLETED the request is immutable.
 */
public enum AnalysisStatus {
    CREATED,
    IN_PROGRESS,
    COMPLETED
}
