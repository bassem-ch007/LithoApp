package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.dto.response.AuditEntryDto;
import com.lithoapp.analysis.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint to query the audit log of an analysis request.
 *
 * The audit log answers:
 *   - Who uploaded the blood test PDF?
 *   - Who replaced the 24h urine PDF?
 *   - Who modified morphSize from X to Y?
 *   - Who completed the request?
 *   - When did each action occur?
 *
 * Entries are returned in chronological order (oldest first).
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditEntryDto>> getAuditLog(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditLog(id));
    }
}
