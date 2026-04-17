package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.dto.request.UpdateStoneResultDto;
import com.lithoapp.analysis.dto.response.StoneResultDto;
import com.lithoapp.analysis.service.StoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for stone analysis structured result management.
 *
 * Base path: /api/analysis-requests/{id}/stone
 *
 * PATCH semantics: only the fields present (non-null) in the request body are updated.
 * Biologists can fill the result progressively across multiple calls.
 * Concurrent updates are protected by optimistic locking (@Version on StoneResult):
 * a 409 Conflict response is returned if two biologists submit updates simultaneously.
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/stone")
@RequiredArgsConstructor
public class StoneController {

    private final StoneService stoneService;

    @PatchMapping
    public ResponseEntity<StoneResultDto> updateStoneResult(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStoneResultDto dto) {
        return ResponseEntity.ok(stoneService.updateStoneResult(id, dto));
    }

    @GetMapping
    public ResponseEntity<StoneResultDto> getStoneResult(@PathVariable Long id) {
        return ResponseEntity.ok(stoneService.getStoneResult(id));
    }
}
