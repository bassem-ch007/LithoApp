package com.lithoapp.analysis.mapper;

import com.lithoapp.analysis.dto.response.MetabolicResultDto;
import com.lithoapp.analysis.dto.response.PdfDocumentDto;
import com.lithoapp.analysis.entity.MetabolicResult;
import com.lithoapp.analysis.entity.PdfDocument;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class MetabolicMapper {

    public PdfDocumentDto toDto(PdfDocument entity) {
        PdfDocumentDto dto = new PdfDocumentDto();
        dto.setId(entity.getId());
        dto.setDocumentType(entity.getDocumentType());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setActive(entity.isActive());
        dto.setOriginalFilename(entity.getOriginalFilename());
        dto.setFileSizeBytes(entity.getFileSizeBytes());
        dto.setUploadedBy(entity.getUploadedBy());
        dto.setUploadedAt(entity.getUploadedAt());
        return dto;
    }

    /**
     * Build a MetabolicResultDto from the full flat list of all versions.
     *
     * @param entity  the MetabolicResult entity
     * @param allDocs all PdfDocument rows (all types, all versions) — expected to be
     *                sorted by documentType ASC, versionNumber DESC
     */
    public MetabolicResultDto toDto(MetabolicResult entity, List<PdfDocument> allDocs) {
        MetabolicResultDto dto = new MetabolicResultDto();
        dto.setId(entity.getId());
        dto.setAnalysisRequestId(entity.getAnalysisRequestId());

        // Active (latest) version per type — at most 3 entries
        List<PdfDocumentDto> latest = allDocs.stream()
                .filter(PdfDocument::isActive)
                .sorted(Comparator.comparing(PdfDocument::getDocumentType))
                .map(this::toDto)
                .toList();
        dto.setLatestDocuments(latest);

        // Full history — all versions, sorted type ASC then version DESC (newest first)
        List<PdfDocumentDto> history = allDocs.stream()
                .sorted(Comparator.comparing(PdfDocument::getDocumentType)
                        .thenComparing(Comparator.comparingInt(PdfDocument::getVersionNumber).reversed()))
                .map(this::toDto)
                .toList();
        dto.setVersionHistory(history);

        // Count distinct types that have an active version
        long activeTypeCount = allDocs.stream()
                .filter(PdfDocument::isActive)
                .map(PdfDocument::getDocumentType)
                .distinct()
                .count();
        dto.setUploadedTypesCount((int) activeTypeCount);

        return dto;
    }
}
