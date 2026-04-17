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
 * Search by DI / DMI / name / phone is intentionally not implemented here.
 * That search requires resolving patient identifiers via the patient-service.
 * It will be added as a separate endpoint once Feign is integrated:
 *   GET /api/analysis-requests/search?di=&dmi=&name=&phone=
 * For now, callers search by patientId directly.
 */
@RestController
@RequestMapping("/api/analysis-requests")
@RequiredArgsConstructor
public class AnalysisRequestController {

    private final AnalysisRequestService analysisRequestService;

    // ── Create ────────────────────────────────────────────────────────────

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
     * List analysis requests with optional filters.
     *
     * Supported query parameters:
     * - patientId  (required when episodeId is absent)
     * - episodeId  (alternative to patientId)
     * - status     (optional filter: CREATED | IN_PROGRESS | COMPLETED)
     *
     * Examples:
     *   GET /api/analysis-requests?patientId=P123
     *   GET /api/analysis-requests?patientId=P123&status=IN_PROGRESS
     *   GET /api/analysis-requests?episodeId=E456
     */
    @GetMapping
    public ResponseEntity<List<AnalysisRequestDto>> list(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String episodeId,
            @RequestParam(required = false) AnalysisStatus status) {

        if (patientId != null) {
            return ResponseEntity.ok(analysisRequestService.listByPatient(patientId, status));
        }
        if (episodeId != null) {
            return ResponseEntity.ok(analysisRequestService.listByEpisode(episodeId));
        }
        if (status != null) {
            return ResponseEntity.ok(analysisRequestService.listByStatus(status));
        }
        // No filter provided — return empty list with informative hint
        // (avoid an unguarded full-table scan in production)
        return ResponseEntity.ok(List.of());
    }

    // ── Complete ──────────────────────────────────────────────────────────

    /**
     * Explicitly mark a request as COMPLETED.
     *
     * METABOLIC: validated — all 3 PDFs must be present.
     * STONE:     validated — finalStoneType must be set.
     *
     * Once completed, the request becomes immutable.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<AnalysisRequestDto> complete(
            @PathVariable Long id,
            @Valid @RequestBody CompleteAnalysisRequestDto dto) {
        return ResponseEntity.ok(analysisRequestService.completeRequest(id, dto));
    }
}
