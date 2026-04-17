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

    @PostMapping
    public ResponseEntity<DrainageResponse> create(
            @Valid @RequestBody CreateDrainageRequest request) {
        DrainageResponse response = drainageService.createDrainage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/drainages/{id} ───────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<DrainageResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(drainageService.getDrainageById(id));
    }

    // ── GET /api/drainages ────────────────────────────────────────────────────
    // Query params: patientId, drainageType, status, overdue

    @GetMapping
    public ResponseEntity<List<DrainageResponse>> getAll(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) DrainageType drainageType,
            @RequestParam(required = false) DrainageStatus status,
            @RequestParam(required = false) Boolean overdue) {

        DrainageFilterRequest filter = new DrainageFilterRequest();
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
