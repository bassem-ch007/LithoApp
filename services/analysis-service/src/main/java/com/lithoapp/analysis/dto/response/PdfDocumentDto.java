package com.lithoapp.analysis.dto.response;

import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PdfDocumentDto {

    private Long id;
    private MetabolicDocumentType documentType;

    /** Monotonically increasing per (request, documentType). Starts at 1. */
    private int versionNumber;

    /** True if this is the current active version for this document type. */
    private boolean isActive;

    private String originalFilename;
    private long fileSizeBytes;
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    // storageKey intentionally NOT exposed — callers use the download endpoint.
}
