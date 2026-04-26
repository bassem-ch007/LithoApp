package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.dto.request.CompleteAnalysisRequestDto;
import com.lithoapp.analysis.dto.request.CreateAnalysisRequestDto;
import com.lithoapp.analysis.dto.response.AnalysisRequestDto;
import com.lithoapp.analysis.service.AnalysisRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 */
@RestController
@RequestMapping("/api/analysis-requests")
@RequiredArgsConstructor
@Tag(name = "Analysis Requests",
     description = "Lifecycle management of biological and stone analysis requests anchored to a clinical episode")
public class AnalysisRequestController {

    private final AnalysisRequestService analysisRequestService;

    // ── Create ────────────────────────────────────────────────────────────

    @Operation(
            summary = "Create a new analysis request",
            description = "Creates an analysis request (METABOLIC or STONE) anchored to an existing episode. " +
                    "Both patientId and episodeId are required; the episode must belong to the patient."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Analysis request created"),
            @ApiResponse(responseCode = "400", description = "Validation error or patient/episode mismatch"),
            @ApiResponse(responseCode = "404", description = "Patient or episode not found")
    })
    @PostMapping
    public ResponseEntity<AnalysisRequestDto> create(@Valid @RequestBody CreateAnalysisRequestDto dto) {
        AnalysisRequestDto created = analysisRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Operation(summary = "Get an analysis request by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analysis request returned"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisRequestDto> getById(
            @Parameter(description = "Analysis request ID") @PathVariable Long id) {
        return ResponseEntity.ok(analysisRequestService.getById(id));
    }

    @Operation(
            summary = "List analysis requests for an episode",
            description = "Primary read endpoint — drives the analysis panel on the episode detail screen. " +
                    "Optionally filter by status (CREATED, IN_PROGRESS, COMPLETED)."
    )
    @ApiResponse(responseCode = "200", description = "List returned (may be empty)")
    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<AnalysisRequestDto>> getByEpisode(
            @Parameter(description = "Episode ID") @PathVariable Long episodeId,
            @Parameter(description = "Optional status filter")
            @RequestParam(required = false) AnalysisStatus status) {
        return ResponseEntity.ok(analysisRequestService.listByEpisode(episodeId, status));
    }

    @Operation(
            summary = "List analysis requests for a patient",
            description = "Secondary read endpoint — all analysis requests for a patient across every episode. " +
                    "Used for the patient-level timeline view."
    )
    @ApiResponse(responseCode = "200", description = "List returned (may be empty)")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AnalysisRequestDto>> getByPatient(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Optional status filter")
            @RequestParam(required = false) AnalysisStatus status) {
        return ResponseEntity.ok(analysisRequestService.listByPatient(patientId, status));
    }

    @Operation(
            summary = "Flexible list filter",
            description = """
                    Flexible filter endpoint. Priority: episodeId > patientId > status-only.
                    If no parameter is provided, an empty list is returned to avoid an unguarded full-table scan.
                    Examples:
                      GET /api/analysis-requests?episodeId=1
                      GET /api/analysis-requests?patientId=1&status=COMPLETED
                      GET /api/analysis-requests?status=CREATED
                    """
    )
    @ApiResponse(responseCode = "200", description = "List returned (empty if no filter supplied)")
    @GetMapping
    public ResponseEntity<List<AnalysisRequestDto>> list(
            @Parameter(description = "Filter by episode (primary axis)")
            @RequestParam(required = false) Long episodeId,
            @Parameter(description = "Filter by patient across all episodes")
            @RequestParam(required = false) Long patientId,
            @Parameter(description = "Filter by status: CREATED | IN_PROGRESS | COMPLETED")
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
        return ResponseEntity.ok(List.of());
    }

    @Operation(
            summary = "Search analysis requests by patient identity",
            description = "Biologist identity search — resolves DI / DMI / name / phone via patient-service (Feign) " +
                    "and returns matching analysis requests. At least one of di, dmi, name, phone must be provided."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching analysis requests"),
            @ApiResponse(responseCode = "400", description = "No identity criterion provided")
    })
    @GetMapping("/search")
    public ResponseEntity<List<AnalysisRequestDto>> searchByPatientIdentity(
            @Parameter(description = "Dossier d'Identité (partial match)")
            @RequestParam(required = false) String di,
            @Parameter(description = "Dossier Médical Informatisé (partial match)")
            @RequestParam(required = false) String dmi,
            @Parameter(description = "Patient first or last name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Patient phone (partial match)")
            @RequestParam(required = false) String phone,
            @Parameter(description = "Optional status filter")
            @RequestParam(required = false) AnalysisStatus status) {

        if (isBlank(di) && isBlank(dmi) && isBlank(name) && isBlank(phone)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                analysisRequestService.searchByPatientIdentity(di, dmi, name, phone, status));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // ── Complete ──────────────────────────────────────────────────────────

    @Operation(
            summary = "Mark an analysis request as COMPLETED",
            description = """
                    Final lifecycle transition. METABOLIC requests should have all 3 PDFs present;
                    STONE requests should have finalStoneType set. Direct CREATED → COMPLETED is blocked —
                    at least one upload or field update must have moved the request to IN_PROGRESS first.
                    Once completed, the request becomes immutable.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Request is in CREATED state or required data missing"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found")
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<AnalysisRequestDto> complete(
            @Parameter(description = "Analysis request ID") @PathVariable Long id,
            @Valid @RequestBody CompleteAnalysisRequestDto dto) {
        return ResponseEntity.ok(analysisRequestService.completeRequest(id, dto));
    }
}
