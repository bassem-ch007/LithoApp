package com.lithoapp.analysis.controller;

import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import com.lithoapp.analysis.dto.response.MetabolicResultDto;
import com.lithoapp.analysis.dto.response.PdfDocumentDto;
import com.lithoapp.analysis.entity.PdfDocument;
import com.lithoapp.analysis.service.MetabolicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoints for metabolic analysis PDF management.
 *
 * Upload semantics: POST handles both first upload and replacement; the service
 * decides internally based on existing data. Replaced files are deleted and an
 * audit entry is written.
 */
@RestController
@RequestMapping("/api/analysis-requests/{id}/metabolic")
@RequiredArgsConstructor
@Tag(name = "Metabolic Analysis",
     description = "Upload, download and inspect metabolic analysis PDFs (blood, 24h urine, spot urine)")
public class MetabolicController {

    private final MetabolicService metabolicService;

    // ── Upload / Replace ──────────────────────────────────────────────────

    @Operation(
            summary = "Upload or replace a metabolic document",
            description = "Accepts multipart/form-data with a PDF file and the biologist ID. " +
                    "If a document of this type already exists, it is replaced (old version archived in audit log)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document uploaded or replaced"),
            @ApiResponse(responseCode = "400", description = "Invalid file (non-PDF, too large, empty)"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found")
    })
    @PostMapping(value = "/documents/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PdfDocumentDto> uploadDocument(
            @Parameter(description = "Analysis request ID") @PathVariable Long id,
            @Parameter(description = "Document type (BLOOD_TEST, URINE_24H, URINE_SPOT)")
            @PathVariable MetabolicDocumentType documentType,
            @Parameter(description = "The PDF file to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Identifier of the biologist performing the upload")
            @RequestParam("biologistId") String biologistId) {

        PdfDocumentDto result = metabolicService.uploadDocument(id, documentType, file, biologistId);
        return ResponseEntity.ok(result);
    }

    // ── Download ──────────────────────────────────────────────────────────

    @Operation(summary = "Download the active (latest) version of a metabolic document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF bytes returned"),
            @ApiResponse(responseCode = "404", description = "Document or analysis request not found")
    })
    @GetMapping("/documents/{documentType}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "Analysis request ID") @PathVariable Long id,
            @Parameter(description = "Document type") @PathVariable MetabolicDocumentType documentType) {

        PdfDocument doc = metabolicService.getActiveDocumentEntity(id, documentType);
        byte[] bytes = metabolicService.downloadDocument(id, documentType);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
                .body(bytes);
    }

    @Operation(summary = "Download a specific historical version of a metabolic document")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF bytes returned"),
            @ApiResponse(responseCode = "404", description = "Version not found")
    })
    @GetMapping("/documents/{documentType}/versions/{version}/download")
    public ResponseEntity<byte[]> downloadDocumentVersion(
            @Parameter(description = "Analysis request ID") @PathVariable Long id,
            @Parameter(description = "Document type") @PathVariable MetabolicDocumentType documentType,
            @Parameter(description = "Version number (1-based)") @PathVariable int version) {

        byte[] bytes = metabolicService.downloadDocumentVersion(id, documentType, version);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + documentType.name() + "_v" + version + ".pdf\"")
                .body(bytes);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Operation(
            summary = "Get the metabolic result view for an analysis request",
            description = "Returns all active metabolic documents and their metadata for the analysis request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metabolic result returned"),
            @ApiResponse(responseCode = "404", description = "Analysis request not found")
    })
    @GetMapping
    public ResponseEntity<MetabolicResultDto> getMetabolicResult(
            @Parameter(description = "Analysis request ID") @PathVariable Long id) {
        return ResponseEntity.ok(metabolicService.getMetabolicResult(id));
    }
}
