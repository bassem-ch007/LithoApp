package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.dto.request.UpdateStoneResultDto;
import com.lithoapp.analysis.dto.response.StoneResultDto;
import com.lithoapp.analysis.security.CurrentUserProvider;
import com.lithoapp.analysis.service.StoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for stone analysis structured result management.
 *
 * PATCH semantics: only non-null fields are updated. Biologists can fill the
 * result progressively. Optimistic locking (@Version) protects concurrent updates —
 * a 409 Conflict is returned if two biologists submit updates simultaneously.
 *
 * The modifiedBy actor is no longer accepted from the client — it is extracted
 * from the authenticated JWT principal to prevent identity spoofing.
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/stone")
@RequiredArgsConstructor
@Tag(name = "Stone Analysis",
     description = "Structured stone-analysis results (morphology, composition, Daudon typing)")
public class StoneController {

    private final StoneService stoneService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Partially update the stone result",
            description = "Only non-null fields in the request are applied. Supports progressive entry. " +
                    "The modifiedBy actor is resolved from the authenticated JWT principal. " +
                    "Returns 409 Conflict if the row has been modified concurrently (optimistic locking)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stone result updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — BIOLOGIST role required"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found"),
            @ApiResponse(responseCode = "409", description = "Concurrent update conflict")
    })
    @PatchMapping
    @PreAuthorize("hasRole('BIOLOGIST')")
    public ResponseEntity<StoneResultDto> updateStoneResult(
            @Parameter(description = "Analysis request ID") @PathVariable Long id,
            @Valid @RequestBody UpdateStoneResultDto dto) {
        String actor = currentUserProvider.getUsername();
        return ResponseEntity.ok(stoneService.updateStoneResult(id, dto, actor));
    }

    @Operation(summary = "Get the current stone result for an analysis request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stone result returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Analysis request or stone result not found")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<StoneResultDto> getStoneResult(
            @Parameter(description = "Analysis request ID") @PathVariable Long id) {
        return ResponseEntity.ok(stoneService.getStoneResult(id));
    }
}
