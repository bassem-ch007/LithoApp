package com.lithoapp.analysis.entity;

import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents one version of a metabolic PDF document.
 *
 * Versioning model:
 * - Every upload (initial or replacement) creates a NEW row — nothing is ever deleted.
 * - {@code versionNumber} is a per-type counter: 1, 2, 3 … incremented on each upload.
 * - {@code isActive} is true only for the most recent version of a given type;
 *   all previous versions have isActive = false and are kept for full history.
 *
 * This means multiple biologists can upload, replace, or re-upload the same type
 * freely, and the full provenance chain is always preserved.
 *
 * No unique constraint on (metabolic_result_id, document_type) — multiple rows
 * per type are expected and intentional.
 */
@Entity
@Table(
    name = "pdf_documents",
    indexes = {
        @Index(name = "idx_pdf_result_type",        columnList = "metabolic_result_id, document_type"),
        @Index(name = "idx_pdf_result_type_active",  columnList = "metabolic_result_id, document_type, is_active")
    }
)
@Getter
@Setter
public class PdfDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metabolic_result_id", nullable = false)
    private Long metabolicResultId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private MetabolicDocumentType documentType;

    /**
     * Monotonically increasing version counter per (metabolicResultId, documentType).
     * First upload = 1, first replacement = 2, second replacement = 3, …
     */
    @Column(nullable = false)
    private int versionNumber;

    /**
     * True for the most recent version of this document type.
     * Only one row per (metabolicResultId, documentType) has isActive = true at any time.
     * Previous versions retain their data and storageKey for full history.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * Key used to retrieve the file from {@code FileStoragePort}.
     * Never deleted — all versions retain their file in storage.
     */
    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private long fileSizeBytes;

    /** Biologist who performed this upload. */
    @Column(nullable = false)
    private String uploadedBy;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}
