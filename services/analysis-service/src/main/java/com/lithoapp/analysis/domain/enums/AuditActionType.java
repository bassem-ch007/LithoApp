package com.lithoapp.analysis.domain.enums;

/**
 * Semantic action types recorded in the audit log.
 * Each represents a distinct, meaningful event in the analysis workflow.
 */
public enum AuditActionType {

    /** A new AnalysisRequest was created by a urologist. */
    REQUEST_CREATED,

    /** The status of a request changed (e.g. CREATED → IN_PROGRESS). */
    STATUS_CHANGED,

    /** A metabolic PDF was uploaded for the first time for a given document type. */
    PDF_UPLOADED,

    /** An existing metabolic PDF was replaced with a new version. */
    PDF_REPLACED,

    /** A StoneResult was initialised (first save). */
    STONE_RESULT_CREATED,

    /** One or more fields of a StoneResult were updated. */
    STONE_RESULT_FIELD_UPDATED,

    /** A request was explicitly marked COMPLETED. */
    REQUEST_COMPLETED
}
