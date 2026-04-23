package com.lithoapp.episodeservice.controller;

import com.lithoapp.episodeservice.dto.request.CreateEpisodeRequest;
import com.lithoapp.episodeservice.dto.request.UpdateEpisodeRequest;
import com.lithoapp.episodeservice.dto.response.EpisodeResponse;
import com.lithoapp.episodeservice.dto.response.EpisodeSummaryResponse;
import com.lithoapp.episodeservice.enums.EpisodeStatus;
import com.lithoapp.episodeservice.service.EpisodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Episodes", description = "Clinical case folder management — one episode per stone event per patient")
@RestController
@RequestMapping("/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @Operation(summary = "Open a new clinical episode (case folder) for a patient")
    @PostMapping
    public ResponseEntity<EpisodeResponse> createEpisode(
            @Valid @RequestBody CreateEpisodeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(episodeService.createEpisode(request));
    }

    @Operation(summary = "Get full episode details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<EpisodeResponse> getEpisodeById(
            @Parameter(description = "Episode ID") @PathVariable Long id) {
        return ResponseEntity.ok(episodeService.getEpisodeById(id));
    }

    @Operation(summary = "Check whether a patient has any linked episodes",
               description = "Returns true if at least one episode exists for the patient. " +
                             "Used by patient-service to guard patient deletion.")
    @GetMapping("/patient/{patientId}/exists")
    public ResponseEntity<Boolean> hasEpisodes(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        return ResponseEntity.ok(episodeService.hasEpisodes(patientId));
    }

    @Operation(
            summary = "List episodes for a patient",
            description = "Returns a paginated timeline of all case folders for a given patient. " +
                    "Use the optional 'status' query parameter to filter by ACTIVE or CLOSED episodes."
    )
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<EpisodeSummaryResponse>> getEpisodesByPatient(
            @Parameter(description = "Patient ID") @PathVariable Long patientId,
            @Parameter(description = "Optional status filter: ACTIVE or CLOSED")
            @RequestParam(required = false) EpisodeStatus status,
            @PageableDefault(size = 10, sort = "openedAt") Pageable pageable) {
        return ResponseEntity.ok(episodeService.getEpisodesByPatient(patientId, status, pageable));
    }

    @Operation(
            summary = "Update episode case-folder details",
            description = "Partial update — only non-null fields are applied. " +
                    "Updatable fields: title, notes, recurrence, status. " +
                    "patientId and openedAt are immutable."
    )
    @PutMapping("/{id}")
    public ResponseEntity<EpisodeResponse> updateEpisode(
            @Parameter(description = "Episode ID") @PathVariable Long id,
            @Valid @RequestBody UpdateEpisodeRequest request) {
        return ResponseEntity.ok(episodeService.updateEpisode(id, request));
    }
}
