package com.lithoapp.analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Container entity for the three metabolic PDF documents.
 * Created automatically when an AnalysisRequest of type METABOLIC is created.
 *
 * The actual PDF metadata lives in {@link PdfDocument} (child records).
 * Completion requires exactly three PdfDocument rows (one per MetabolicDocumentType).
 */
@Entity
@Table(name = "metabolic_results")
@Getter
@Setter
public class MetabolicResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Plain FK to the owning AnalysisRequest.
     * No @OneToOne association — services join explicitly to keep the
     * aggregate boundary clean and avoid accidental eager loading.
     */
    @Column(nullable = false, unique = true)
    private Long analysisRequestId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ── Factory ───────────────────────────────────────────────────────────

    public static MetabolicResult forRequest(Long analysisRequestId) {
        MetabolicResult mr = new MetabolicResult();
        mr.analysisRequestId = analysisRequestId;
        mr.createdAt = LocalDateTime.now();
        return mr;
    }
}
