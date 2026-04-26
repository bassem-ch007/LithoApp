package com.lithoapp.drainage.controller;

import com.lithoapp.drainage.dto.*;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.service.DrainageService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/drainages")
@RequiredArgsConstructor
@Tag(name = "Drainages",
     description = "Management of urinary drainage devices (JJ stents, nephrostomies, catheters) attached to a clinical episode")
public class DrainageController {

    private final DrainageService drainageService;

    // ── POST /api/drainages ───────────────────────────────────────────────────

    @Operation(
            summary = "Create a new drainage",
            description = "Creates a drainage device placement. The episodeId is required in the body; " +
                    "the episode must exist and belong to the referenced patient."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Drainage created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Episode or patient not found")
    })
    @PostMapping
    public ResponseEntity<DrainageResponse> create(
            @Valid @RequestBody CreateDrainageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(drainageService.createDrainage(request));
    }

    // ── GET /api/drainages/{id} ───────────────────────────────────────────────

    @Operation(summary = "Get a drainage by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drainage returned"),
            @ApiResponse(responseCode = "404", description = "Drainage not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DrainageResponse> getById(
            @Parameter(description = "Drainage UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(drainageService.getDrainageById(id));
    }

    // ── GET /api/drainages/episode/{episodeId} ────────────────────────────────

    @Operation(
            summary = "List drainages for an episode",
            description = "Primary read endpoint — drives the drainage panel on the episode detail screen."
    )
    @ApiResponse(responseCode = "200", description = "List returned (may be empty)")
    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<DrainageResponse>> getByEpisode(
            @Parameter(description = "Episode ID") @PathVariable Long episodeId) {
        return ResponseEntity.ok(drainageService.getDrainagesByEpisodeId(episodeId));
    }

    // ── GET /api/drainages/patient/{patientId} ────────────────────────────────

    @Operation(
            summary = "List drainages for a patient",
            description = "Secondary read endpoint — full drainage timeline across every episode of a patient."
    )
    @ApiResponse(responseCode = "200", description = "List returned (may be empty)")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DrainageResponse>> getByPatient(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        return ResponseEntity.ok(drainageService.getDrainagesByPatientId(patientId));
    }

    // ── GET /api/drainages ────────────────────────────────────────────────────

    @Operation(
            summary = "Flexible drainage list filter",
            description = "Combine any of: episodeId, patientId, drainageType, status, overdue. All parameters are optional."
    )
    @ApiResponse(responseCode = "200", description = "Filtered list returned")
    @GetMapping
    public ResponseEntity<List<DrainageResponse>> getAll(
            @Parameter(description = "Filter by episode ID")
            @RequestParam(required = false) Long episodeId,
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) Long patientId,
            @Parameter(description = "Filter by drainage type (JJ_STENT, NEPHROSTOMY, CATHETER, …)")
            @RequestParam(required = false) DrainageType drainageType,
            @Parameter(description = "Filter by status (ACTIVE, REMOVED, …)")
            @RequestParam(required = false) DrainageStatus status,
            @Parameter(description = "If true, return only drainages past their planned removal date")
            @RequestParam(required = false) Boolean overdue) {

        DrainageFilterRequest filter = new DrainageFilterRequest();
        filter.setEpisodeId(episodeId);
        filter.setPatientId(patientId);
        filter.setDrainageType(drainageType);
        filter.setStatus(status);
        filter.setOverdue(overdue);

        return ResponseEntity.ok(drainageService.getDrainages(filter));
    }

    // ── PUT /api/drainages/{id} ───────────────────────────────────────────────

    @Operation(
            summary = "Update a drainage",
            description = "Partial update of drainage metadata (planned removal date, notes, device details). " +
                    "The episodeId is immutable."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drainage updated"),
            @ApiResponse(responseCode = "404", description = "Drainage not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DrainageResponse> update(
            @Parameter(description = "Drainage UUID") @PathVariable UUID id,
            @RequestBody UpdateDrainageRequest request) {
        return ResponseEntity.ok(drainageService.updateDrainage(id, request));
    }

    // ── PATCH /api/drainages/{id}/remove ─────────────────────────────────────

    @Operation(
            summary = "Mark a drainage as removed",
            description = "Records the removal of the drainage device (actual removal date and notes). " +
                    "Transitions the drainage status to REMOVED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drainage marked as removed"),
            @ApiResponse(responseCode = "400", description = "Drainage already removed or invalid removal data"),
            @ApiResponse(responseCode = "404", description = "Drainage not found")
    })
    @PatchMapping("/{id}/remove")
    public ResponseEntity<DrainageResponse> remove(
            @Parameter(description = "Drainage UUID") @PathVariable UUID id,
            @Valid @RequestBody RemoveDrainageRequest request) {
        return ResponseEntity.ok(drainageService.removeDrainage(id, request));
    }
}
