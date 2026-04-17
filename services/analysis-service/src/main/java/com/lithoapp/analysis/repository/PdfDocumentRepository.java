package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import com.lithoapp.analysis.entity.PdfDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {

    // ── All versions ──────────────────────────────────────────────────────

    /** All versions for a metabolic result, sorted by type then version descending. */
    List<PdfDocument> findByMetabolicResultIdOrderByDocumentTypeAscVersionNumberDesc(Long metabolicResultId);

    /** All versions for a specific (result, type) pair, newest first. */
    List<PdfDocument> findByMetabolicResultIdAndDocumentTypeOrderByVersionNumberDesc(
            Long metabolicResultId, MetabolicDocumentType documentType);

    // ── Active (latest) version ───────────────────────────────────────────

    /** The single active version for a given (result, type), if any. */
    Optional<PdfDocument> findByMetabolicResultIdAndDocumentTypeAndIsActiveTrue(
            Long metabolicResultId, MetabolicDocumentType documentType);

    /** All active versions across all types for a metabolic result. */
    List<PdfDocument> findByMetabolicResultIdAndIsActiveTrue(Long metabolicResultId);

    // ── Version number calculation ────────────────────────────────────────

    /** Used to compute the next version number: max(versionNumber) + 1. */
    Optional<PdfDocument> findTopByMetabolicResultIdAndDocumentTypeOrderByVersionNumberDesc(
            Long metabolicResultId, MetabolicDocumentType documentType);

    // ── Bulk deactivation ─────────────────────────────────────────────────

    /**
     * Marks all active versions of a given (result, type) as inactive before
     * inserting a new version. Done with a bulk UPDATE to avoid loading every
     * row into the persistence context.
     */
    @Modifying
    @Query("UPDATE PdfDocument p SET p.isActive = false " +
           "WHERE p.metabolicResultId = :resultId AND p.documentType = :type AND p.isActive = true")
    void deactivateAllForType(@Param("resultId") Long metabolicResultId,
                              @Param("type") MetabolicDocumentType documentType);

    // ── Counts ────────────────────────────────────────────────────────────

    /** Number of distinct document types that have at least one active version. */
    @Query("SELECT COUNT(DISTINCT p.documentType) FROM PdfDocument p " +
           "WHERE p.metabolicResultId = :resultId AND p.isActive = true")
    long countActiveTypes(@Param("resultId") Long metabolicResultId);

    /** Total number of versions (all types, all history) for a metabolic result. */
    long countByMetabolicResultId(Long metabolicResultId);
}
