package com.lithoapp.analysis.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MetabolicResultDto {

    private Long id;
    private Long analysisRequestId;

    /**
     * The current active (latest) version for each document type that has been uploaded.
     * At most 3 entries (one per MetabolicDocumentType).
     */
    private List<PdfDocumentDto> latestDocuments;

    /**
     * Full version history across all document types, ordered by type then version
     * descending (newest first within each type).
     * Includes both active and superseded versions.
     */
    private List<PdfDocumentDto> versionHistory;

    /**
     * How many distinct document types have at least one active version.
     * Range: 0–3. Informational only — completion is not gated on this value.
     */
    private int uploadedTypesCount;
}
