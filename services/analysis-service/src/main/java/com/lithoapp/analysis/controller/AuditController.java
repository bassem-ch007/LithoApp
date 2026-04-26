package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.dto.response.AuditEntryDto;
import com.lithoapp.analysis.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint to query the audit log of an analysis request.
 *
 * The audit log answers: who uploaded / replaced a PDF, who modified a field,
 * who completed the request, and when each action occurred. Entries are returned
 * in chronological order (oldest first).
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/audit")
@RequiredArgsConstructor
@Tag(name = "Analysis Audit",
     description = "Chronological audit trail of all mutations performed on an analysis request")
public class AuditController {

    private final AuditService auditService;

    @Operation(
            summary = "Get the audit log for an analysis request",
            description = "Returns every uploaded/replaced PDF, field modification and lifecycle transition " +
                    "applied to the analysis request, in chronological order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log returned (may be empty)"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found")
    })
    @GetMapping
    public ResponseEntity<List<AuditEntryDto>> getAuditLog(
            @Parameter(description = "Analysis request ID") @PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditLog(id));
    }
}
