package com.lithoapp.analysis.service;

import com.lithoapp.analysis.domain.enums.AnalysisType;
import com.lithoapp.analysis.domain.enums.AuditActionType;
import com.lithoapp.analysis.domain.enums.MetabolicDocumentType;
import com.lithoapp.analysis.dto.response.MetabolicResultDto;
import com.lithoapp.analysis.dto.response.PdfDocumentDto;
import com.lithoapp.analysis.entity.AnalysisRequest;
import com.lithoapp.analysis.entity.MetabolicResult;
import com.lithoapp.analysis.entity.PdfDocument;
import com.lithoapp.analysis.exception.AnalysisNotFoundException;
import com.lithoapp.analysis.exception.InvalidAnalysisTypeOperationException;
import com.lithoapp.analysis.exception.StorageException;
import com.lithoapp.analysis.mapper.MetabolicMapper;
import com.lithoapp.analysis.port.FileStoragePort;
import com.lithoapp.analysis.repository.MetabolicResultRepository;
import com.lithoapp.analysis.repository.PdfDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetabolicService {

    private final AnalysisRequestService requestService;
    private final MetabolicResultRepository metabolicResultRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final FileStoragePort fileStoragePort;
    private final AuditService auditService;
    private final MetabolicMapper metabolicMapper;

    // ── Upload (versioned) ────────────────────────────────────────────────

    /**
     * Upload a new version of a metabolic PDF for the given document type.
     *
     * Versioning behaviour:
     * - Every call creates a NEW {@link PdfDocument} row — existing rows are never deleted.
     * - The previously active version (if any) is marked {@code isActive = false}.
     * - The new row gets {@code isActive = true} and {@code versionNumber = previous + 1}.
     * - Both the old file and the new file are kept in storage — full version history
     *   is preserved including all binary content.
     *
     * Collaboration:
     * - Any biologist can upload at any time as long as the request is not COMPLETED.
     * - No ownership — biologists can freely replace each other's uploads.
     *
     * Audit:
     * - First upload of a type → {@code PDF_UPLOADED}
     * - Subsequent uploads of the same type → {@code PDF_REPLACED}
     *   with oldValue = previous storageKey, newValue = new storageKey.
     */
    @Transactional
    public PdfDocumentDto uploadDocument(Long requestId, MetabolicDocumentType documentType,
                                         MultipartFile file, String biologistId) {
        AnalysisRequest request = requestService.loadOrThrow(requestId);
        request.guardNotCompleted();

        if (request.getType() != AnalysisType.METABOLIC) {
            throw new InvalidAnalysisTypeOperationException(
                    "Cannot upload metabolic PDF: request " + requestId + " is of type " + request.getType());
        }

        MetabolicResult mr = metabolicResultRepository.findByAnalysisRequestId(requestId)
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));

        // ── Resolve previous active version ───────────────────────────────
        boolean hasExistingVersion = pdfDocumentRepository
                .findByMetabolicResultIdAndDocumentTypeAndIsActiveTrue(mr.getId(), documentType)
                .isPresent();

        String previousStorageKey = hasExistingVersion
                ? pdfDocumentRepository
                        .findByMetabolicResultIdAndDocumentTypeAndIsActiveTrue(mr.getId(), documentType)
                        .map(PdfDocument::getStorageKey)
                        .orElse(null)
                : null;

        // ── Compute next version number ───────────────────────────────────
        int nextVersion = pdfDocumentRepository
                .findTopByMetabolicResultIdAndDocumentTypeOrderByVersionNumberDesc(mr.getId(), documentType)
                .map(d -> d.getVersionNumber() + 1)
                .orElse(1);

        // ── Deactivate all previous active versions for this type ─────────
        if (hasExistingVersion) {
            pdfDocumentRepository.deactivateAllForType(mr.getId(), documentType);
            log.debug("Deactivated previous active version of {} for request {}", documentType, requestId);
        }

        // ── Store new file (old file kept in storage) ─────────────────────
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new StorageException("Failed to read uploaded file", e);
        }
        String storageKey = fileStoragePort.store(bytes, file.getOriginalFilename());

        // ── Persist new version row ───────────────────────────────────────
        PdfDocument doc = new PdfDocument();
        doc.setMetabolicResultId(mr.getId());
        doc.setDocumentType(documentType);
        doc.setVersionNumber(nextVersion);
        doc.setActive(true);
        doc.setStorageKey(storageKey);
        doc.setOriginalFilename(file.getOriginalFilename() != null
                ? file.getOriginalFilename() : documentType.name() + ".pdf");
        doc.setFileSizeBytes(bytes.length);
        doc.setUploadedBy(biologistId);
        doc.setUploadedAt(LocalDateTime.now());
        pdfDocumentRepository.save(doc);

        // ── Audit ─────────────────────────────────────────────────────────
        AuditActionType auditAction = hasExistingVersion
                ? AuditActionType.PDF_REPLACED
                : AuditActionType.PDF_UPLOADED;
        auditService.record(requestId, biologistId, auditAction,
                documentType.name(), previousStorageKey, storageKey);

        log.info("Uploaded {} PDF v{} for request {} by {}",
                documentType, nextVersion, requestId, biologistId);

        // ── Auto-transition CREATED → IN_PROGRESS ─────────────────────────
        requestService.transitionToInProgressIfNeeded(request, biologistId);

        return metabolicMapper.toDto(doc);
    }

    // ── Download ──────────────────────────────────────────────────────────

    /**
     * Download the active (latest) version of a document type.
     */
    @Transactional(readOnly = true)
    public byte[] downloadDocument(Long requestId, MetabolicDocumentType documentType) {
        PdfDocument doc = getActiveDocumentEntity(requestId, documentType);
        return fileStoragePort.retrieve(doc.getStorageKey());
    }

    /**
     * Download a specific historical version of a document type.
     */
    @Transactional(readOnly = true)
    public byte[] downloadDocumentVersion(Long requestId, MetabolicDocumentType documentType, int version) {
        MetabolicResult mr = loadMetabolicResult(requestId);
        PdfDocument doc = pdfDocumentRepository
                .findByMetabolicResultIdAndDocumentTypeOrderByVersionNumberDesc(mr.getId(), documentType)
                .stream()
                .filter(d -> d.getVersionNumber() == version)
                .findFirst()
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));
        return fileStoragePort.retrieve(doc.getStorageKey());
    }

    /** Returns the active document entity (used by the download endpoint for metadata). */
    @Transactional(readOnly = true)
    public PdfDocument getActiveDocumentEntity(Long requestId, MetabolicDocumentType documentType) {
        MetabolicResult mr = loadMetabolicResult(requestId);
        return pdfDocumentRepository
                .findByMetabolicResultIdAndDocumentTypeAndIsActiveTrue(mr.getId(), documentType)
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MetabolicResultDto getMetabolicResult(Long requestId) {
        AnalysisRequest request = requestService.loadOrThrow(requestId);
        if (request.getType() != AnalysisType.METABOLIC) {
            throw new InvalidAnalysisTypeOperationException(
                    "Request " + requestId + " is not of type METABOLIC");
        }
        MetabolicResult mr = loadMetabolicResult(requestId);
        List<PdfDocument> allDocs = pdfDocumentRepository
                .findByMetabolicResultIdOrderByDocumentTypeAscVersionNumberDesc(mr.getId());
        return metabolicMapper.toDto(mr, allDocs);
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private MetabolicResult loadMetabolicResult(Long requestId) {
        return metabolicResultRepository.findByAnalysisRequestId(requestId)
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));
    }
}
