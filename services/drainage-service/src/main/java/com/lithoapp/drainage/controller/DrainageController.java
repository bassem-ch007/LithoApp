package com.lithoapp.drainage.controller;

import com.lithoapp.drainage.dto.*;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.service.DrainageService;
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
public class DrainageController {

    private final DrainageService drainageService;

    // ── POST /api/drainages ───────────────────────────────────────────────────
    // episodeId is required in the request body.

    @PostMapping
    public ResponseEntity<DrainageResponse> create(
            @Valid @RequestBody CreateDrainageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(drainageService.createDrainage(request));
    }

    // ── GET /api/drainages/{id} ───────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<DrainageResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(drainageService.getDrainageById(id));
    }

    // ── GET /api/drainages/episode/{episodeId} ────────────────────────────────
    // Primary read endpoint — drives the drainage panel on the episode detail screen.

    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<List<DrainageResponse>> getByEpisode(
            @PathVariable Long episodeId) {
        return ResponseEntity.ok(drainageService.getDrainagesByEpisodeId(episodeId));
    }

    // ── GET /api/drainages/patient/{patientId} ────────────────────────────────
    // Secondary read endpoint — patient-level drainage timeline across all episodes.

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DrainageResponse>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(drainageService.getDrainagesByPatientId(patientId));
    }

    // ── GET /api/drainages ────────────────────────────────────────────────────
    // Flexible filter: episodeId, patientId, drainageType, status, overdue

    @GetMapping
    public ResponseEntity<List<DrainageResponse>> getAll(
            @RequestParam(required = false) Long episodeId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) DrainageType drainageType,
            @RequestParam(required = false) DrainageStatus status,
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

    @PutMapping("/{id}")
    public ResponseEntity<DrainageResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateDrainageRequest request) {
        return ResponseEntity.ok(drainageService.updateDrainage(id, request));
    }

    // ── PATCH /api/drainages/{id}/remove ─────────────────────────────────────

    @PatchMapping("/{id}/remove")
    public ResponseEntity<DrainageResponse> remove(
            @PathVariable UUID id,
            @Valid @RequestBody RemoveDrainageRequest request) {
        return ResponseEntity.ok(drainageService.removeDrainage(id, request));
    }
}
