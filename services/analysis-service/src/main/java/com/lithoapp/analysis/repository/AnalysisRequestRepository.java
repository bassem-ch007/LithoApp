package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.entity.AnalysisRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRequestRepository extends JpaRepository<AnalysisRequest, Long> {

    // ── Patient axis ──────────────────────────────────────────────────────

    List<AnalysisRequest> findByPatientId(Long patientId);

    List<AnalysisRequest> findByPatientIdAndStatus(Long patientId, AnalysisStatus status);

    // ── Episode axis (primary read) ───────────────────────────────────────

    /**
     * Returns all analysis requests for a given episode.
     * This is the primary read operation — drives the analysis panel on the episode detail screen.
     */
    List<AnalysisRequest> findByEpisodeId(Long episodeId);

    /**
     * Returns all analysis requests for a given episode filtered by status.
     * Consistent with the patient-axis query that also supports status filtering.
     */
    List<AnalysisRequest> findByEpisodeIdAndStatus(Long episodeId, AnalysisStatus status);

    // ── Status axis (lab/admin view) ──────────────────────────────────────

    List<AnalysisRequest> findByStatus(AnalysisStatus status);

    // ── Future extension hook ─────────────────────────────────────────────
    // When the patient-service Feign client is available, a SearchFacadeService
    // will resolve DI / DMI / name / phone → patientId externally, then call
    // findByPatientIdIn(List<Long>). No changes required to this repository.
}
