package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.entity.AnalysisRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRequestRepository extends JpaRepository<AnalysisRequest, Long> {

    List<AnalysisRequest> findByPatientId(String patientId);

    List<AnalysisRequest> findByEpisodeId(String episodeId);

    List<AnalysisRequest> findByPatientIdAndStatus(String patientId, AnalysisStatus status);

    List<AnalysisRequest> findByStatus(AnalysisStatus status);

    // ── Future search hook ─────────────────────────────────────────────────
    // When the patient-service Feign client is available, a SearchFacadeService
    // will resolve DI / DMI / name / phone → patientId externally, then call
    // findByPatientIdIn(List<String>). No changes required to this repository.
}
