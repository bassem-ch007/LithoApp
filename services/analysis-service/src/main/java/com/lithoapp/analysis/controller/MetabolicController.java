package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import com.lithoapp.analysis.dto.response.MetabolicResultDto;
import com.lithoapp.analysis.dto.response.PdfDocumentDto;
import com.lithoapp.analysis.entity.PdfDocument;
import com.lithoapp.analysis.service.MetabolicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoints for metabolic analysis PDF management.
 *
 * Base path: /api/analysis-requests/{id}/metabolic
 *
 * Upload semantics:
 *   POST /api/analysis-requests/{id}/metabolic/documents/{type}
 *     - Accepts multipart/form-data with fields: file (required), biologistId (required)
 *     - If no document of this type exists: uploads it
 *     - If a document already exists: replaces it (old file deleted, audit entry written)
 *   Using POST for both cases keeps the API simple; the service determines
 *   upload-vs-replace internally based on existing data.
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/metabolic")
@RequiredArgsConstructor
public class MetabolicController {

    private final MetabolicService metabolicService;

    // ── Upload / Replace ──────────────────────────────────────────────────

    @PostMapping(value = "/documents/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PdfDocumentDto> uploadDocument(
            @PathVariable Long id,
            @PathVariable MetabolicDocumentType documentType,
            @RequestParam("file") MultipartFile file,
            @RequestParam("biologistId") String biologistId) {

        PdfDocumentDto result = metabolicService.uploadDocument(id, documentType, file, biologistId);
        return ResponseEntity.ok(result);
    }

    // ── Download ──────────────────────────────────────────────────────────

    /** Download the active (latest) version of a document type. */
    @GetMapping("/documents/{documentType}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            @PathVariable MetabolicDocumentType documentType) {

        PdfDocument doc = metabolicService.getActiveDocumentEntity(id, documentType);
        byte[] bytes = metabolicService.downloadDocument(id, documentType);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
                .body(bytes);
    }

    /** Download a specific historical version of a document type. */
    @GetMapping("/documents/{documentType}/versions/{version}/download")
    public ResponseEntity<byte[]> downloadDocumentVersion(
            @PathVariable Long id,
            @PathVariable MetabolicDocumentType documentType,
            @PathVariable int version) {

        byte[] bytes = metabolicService.downloadDocumentVersion(id, documentType, version);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + documentType.name() + "_v" + version + ".pdf\"")
                .body(bytes);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<MetabolicResultDto> getMetabolicResult(@PathVariable Long id) {
        return ResponseEntity.ok(metabolicService.getMetabolicResult(id));
    }
}
