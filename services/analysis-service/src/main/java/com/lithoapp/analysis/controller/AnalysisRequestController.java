package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.dto.request.CompleteAnalysisRequestDto;
import com.lithoapp.analysis.dto.request.CreateAnalysisRequestDto;
import com.lithoapp.analysis.dto.response.AnalysisRequestDto;
import com.lithoapp.analysis.service.AnalysisRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for analysis request lifecycle management.
 *
 * Base path: /api/analysis-requests
 *
 * Workflow: patient → episode → analysis
 * Every analysis request is anchored to an episode. The primary read path
 * is GET /episode/{episodeId}. Patient-level reads span all episodes of a patient.
 *
 * Search by DI / DMI / name / phone is intentionally not implemented here.
 * That search requires resolving patient identifiers via the patient-service.
 * It will be added as a separate endpoint once Feign is integrated:
 *   GET /api/analysis-requests/search?di=&dmi=&name=&phone=
 * For now, callers search by patientId or episodeId directly.
 */
@RestController
@RequestMapping("/api/analysis-requests")
@RequiredArgsConstructor
public class AnalysisRequestController {

    private final AnalysisRequestService analysisRequestService;

    // ── Create ────────────────────────────────────────────────────────────

    /**
     * Create a new analysis request.
     * Both patientId and episodeId are required.
     * The episode must belong to the patient (validated by EpisodeValidationService).
     */
    @PostMapping
    public ResponseEntity<AnalysisRequestDto> create(@Valid @RequestBody CreateAnalysisRequestDto dto) {
        AnalysisRequestDto created = analysisRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisRequestService.getById(id));
    }

    /**
     * Primary read endpoint — all analysis requests for a specific episode.
     * Drives the analysis panel on the episode detail screen.
     *
     * GET /api/analysis-requests/episode/{episodeId}
     * GET /api/analysis-requests/episode/{episodeId}?status=IN_PROGRESS
     */
    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<AnalysisRequestDto>> getByEpisode(
            @PathVariable Long episodeId,
            @RequestParam(required = false) AnalysisStatus status) {
        return ResponseEntity.ok(analysisRequestService.listByEpisode(episodeId, status));
    }

    /**
     * Secondary read endpoint — all analysis requests for a patient across all episodes.
     * Used for the patient-level timeline view.
     *
     * GET /api/analysis-requests/patient/{patientId}
     * GET /api/analysis-requests/patient/{patientId}?status=COMPLETED
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AnalysisRequestDto>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) AnalysisStatus status) {
        return ResponseEntity.ok(analysisRequestService.listByPatient(patientId, status));
    }

    /**
     * Flexible filter endpoint.
     *
     * Supported query parameters (all optional, but at least one should be provided):
     *   episodeId    — filter by episode (primary axis)
     *   patientId    — filter by patient across all episodes (secondary axis)
     *   status       — CREATED | IN_PROGRESS | COMPLETED
     *
     * Priority: episodeId > patientId > status-only.
     * If none are provided, an empty list is returned to avoid an unguarded full-table scan.
     *
     * Examples:
     *   GET /api/analysis-requests?episodeId=1
     *   GET /api/analysis-requests?episodeId=1&status=IN_PROGRESS
     *   GET /api/analysis-requests?patientId=1&status=COMPLETED
     *   GET /api/analysis-requests?status=CREATED
     */
    @GetMapping
    public ResponseEntity<List<AnalysisRequestDto>> list(
            @RequestParam(required = false) Long episodeId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) AnalysisStatus status) {

        if (episodeId != null) {
            return ResponseEntity.ok(analysisRequestService.listByEpisode(episodeId, status));
        }
        if (patientId != null) {
            return ResponseEntity.ok(analysisRequestService.listByPatient(patientId, status));
        }
        if (status != null) {
            return ResponseEntity.ok(analysisRequestService.listByStatus(status));
        }
        // No filter provided — return empty list to avoid full-table scan
        return ResponseEntity.ok(List.of());
    }

    // ── Complete ──────────────────────────────────────────────────────────

    /**
     * Explicitly mark a request as COMPLETED.
     *
     * METABOLIC: all 3 PDFs should be present (biologist's responsibility).
     * STONE:     finalStoneType should be set (biologist's responsibility).
     *
     * CREATED → COMPLETED is blocked. At least one upload or field update
     * must have occurred (auto-transitioning to IN_PROGRESS) before completing.
     * Once completed, the request becomes immutable.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<AnalysisRequestDto> complete(
            @PathVariable Long id,
            @Valid @RequestBody CompleteAnalysisRequestDto dto) {
        return ResponseEntity.ok(analysisRequestService.completeRequest(id, dto));
    }
}
