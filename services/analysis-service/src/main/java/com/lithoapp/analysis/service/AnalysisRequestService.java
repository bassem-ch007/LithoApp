package com.lithoapp.analysis.service;

import com.lithoapp.analysis.domain.enums.AnalysisStatus;
import com.lithoapp.analysis.domain.enums.AnalysisType;
import com.lithoapp.analysis.domain.enums.AuditActionType;
import com.lithoapp.analysis.dto.request.CompleteAnalysisRequestDto;
import com.lithoapp.analysis.dto.request.CreateAnalysisRequestDto;
import com.lithoapp.analysis.dto.response.AnalysisRequestDto;
import com.lithoapp.analysis.entity.AnalysisRequest;
import com.lithoapp.analysis.entity.MetabolicResult;
import com.lithoapp.analysis.entity.StoneResult;
import com.lithoapp.analysis.exception.AnalysisNotFoundException;
import com.lithoapp.analysis.exception.CompletionNotAllowedException;
import com.lithoapp.analysis.mapper.AnalysisRequestMapper;
import com.lithoapp.analysis.mapper.MetabolicMapper;
import com.lithoapp.analysis.mapper.StoneResultMapper;
import com.lithoapp.analysis.repository.*;
import com.lithoapp.analysis.service.validation.EpisodeValidationService;
import com.lithoapp.analysis.service.validation.PatientValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisRequestService {

    private final AnalysisRequestRepository requestRepository;
    private final MetabolicResultRepository metabolicResultRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final StoneResultRepository stoneResultRepository;
    private final AuditEntryRepository auditEntryRepository;
    private final AuditService auditService;
    private final AnalysisRequestMapper requestMapper;
    private final MetabolicMapper metabolicMapper;
    private final StoneResultMapper stoneResultMapper;

    /**
     * Cross-service validation — stub in the current phase.
     * Replace the stub with a @Primary Feign-backed implementation when ready.
     */
    private final PatientValidationService patientValidationService;

    /**
     * Cross-service validation — stub in the current phase.
     * Validates that the episode exists AND belongs to the given patient.
     * Replace the stub with a @Primary Feign-backed implementation when ready.
     */
    private final EpisodeValidationService episodeValidationService;

    // ── Create ────────────────────────────────────────────────────────────

    /**
     * Creates a new analysis request anchored to the given episode.
     *
     * Validation order (each step throws a typed exception on failure):
     * 1. Bean validation — patientId, episodeId, createdBy, type all present (@Valid on controller).
     * 2. Patient exists — PatientValidationService (stub → future Feign).
     * 3. Episode exists AND belongs to the patient — EpisodeValidationService (stub → future Feign).
     * 4. Create AnalysisRequest + result stub + audit entry.
     */
    @Transactional
    public AnalysisRequestDto createRequest(CreateAnalysisRequestDto dto) {

        // Step 2 — patient existence (future Feign guard)
        patientValidationService.validatePatientExists(dto.getPatientId());

        // Step 3 — episode existence + patient/episode consistency
        episodeValidationService.validateEpisodeBelongsToPatient(
                dto.getEpisodeId(), dto.getPatientId());

        AnalysisRequest request = AnalysisRequest.create(
                dto.getPatientId(), dto.getEpisodeId(), dto.getCreatedBy(), dto.getType());
        requestRepository.save(request);

        if (request.getType() == AnalysisType.METABOLIC) {
            metabolicResultRepository.save(MetabolicResult.forRequest(request.getId()));
        } else {
            stoneResultRepository.save(StoneResult.forRequest(request.getId()));
        }

        auditService.record(request.getId(), dto.getCreatedBy(),
                AuditActionType.REQUEST_CREATED, null, null, request.getType().name());

        log.info("Created {} analysis request id={} for episode={} patient={}",
                request.getType(), request.getId(),
                request.getEpisodeId(), request.getPatientId());

        return buildDetailDto(request);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AnalysisRequestDto getById(Long id) {
        return buildDetailDto(loadOrThrow(id));
    }

    /**
     * Returns all analysis requests for a patient across all episodes.
     * Used for patient-level overview and reporting.
     *
     * @param patientId the patient to filter by
     * @param status    optional — if null, all statuses are returned
     */
    @Transactional(readOnly = true)
    public List<AnalysisRequestDto> listByPatient(Long patientId, AnalysisStatus status) {
        List<AnalysisRequest> requests = (status != null)
                ? requestRepository.findByPatientIdAndStatus(patientId, status)
                : requestRepository.findByPatientId(patientId);
        return requests.stream().map(this::buildDetailDto).toList();
    }

    /**
     * Returns all analysis requests for a specific episode.
     * This is the primary read operation — drives the analysis panel on the episode detail screen.
     *
     * @param episodeId the episode to filter by
     * @param status    optional — if null, all statuses are returned
     */
    @Transactional(readOnly = true)
    public List<AnalysisRequestDto> listByEpisode(Long episodeId, AnalysisStatus status) {
        List<AnalysisRequest> requests = (status != null)
                ? requestRepository.findByEpisodeIdAndStatus(episodeId, status)
                : requestRepository.findByEpisodeId(episodeId);
        return requests.stream().map(this::buildDetailDto).toList();
    }

    /**
     * Returns all analysis requests with the given status (lab/admin view).
     */
    @Transactional(readOnly = true)
    public List<AnalysisRequestDto> listByStatus(AnalysisStatus status) {
        return requestRepository.findByStatus(status)
                .stream().map(this::buildDetailDto).toList();
    }

    // ── Complete ──────────────────────────────────────────────────────────

    /**
     * Mark a request as COMPLETED.
     *
     * The only enforced rule is that the request must already be IN_PROGRESS,
     * meaning at least one result contribution has been made. No content
     * completeness is checked — the biologist decides when the bilan is done.
     *
     * CREATED → COMPLETED is intentionally blocked: at least one upload or
     * field update must have occurred first (auto-transitioning to IN_PROGRESS).
     */
    @Transactional
    public AnalysisRequestDto completeRequest(Long id, CompleteAnalysisRequestDto dto) {
        AnalysisRequest request = loadOrThrow(id);
        request.guardNotCompleted();

        if (request.getStatus() == AnalysisStatus.CREATED) {
            throw new CompletionNotAllowedException(
                    "Request " + id + " has no contributions yet (status is CREATED). " +
                    "At least one PDF upload or field update is required before completing.");
        }

        AnalysisStatus previousStatus = request.getStatus();
        request.transitionTo(AnalysisStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        request.setCompletedBy(dto.getCompletedBy());
        requestRepository.save(request);

        auditService.record(id, dto.getCompletedBy(),
                AuditActionType.STATUS_CHANGED, "status",
                previousStatus.name(), AnalysisStatus.COMPLETED.name());
        auditService.record(id, dto.getCompletedBy(),
                AuditActionType.REQUEST_COMPLETED, null, null, dto.getCompletedBy());

        log.info("Completed analysis request id={} by {}", id, dto.getCompletedBy());
        return buildDetailDto(request);
    }

    // ── Internal helpers ──────────────────────────────────────────────────

    public AnalysisRequest loadOrThrow(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(id));
    }

    /**
     * Auto-transition to IN_PROGRESS on the first result contribution.
     * Called by MetabolicService and StoneService within the same transaction.
     */
    public void transitionToInProgressIfNeeded(AnalysisRequest request, String actorId) {
        if (request.getStatus() == AnalysisStatus.CREATED) {
            AnalysisStatus previous = request.getStatus();
            request.transitionTo(AnalysisStatus.IN_PROGRESS);
            requestRepository.save(request);
            auditService.record(request.getId(), actorId,
                    AuditActionType.STATUS_CHANGED, "status",
                    previous.name(), AnalysisStatus.IN_PROGRESS.name());
        }
    }

    // ── DTO assembly ──────────────────────────────────────────────────────

    private AnalysisRequestDto buildDetailDto(AnalysisRequest request) {
        if (request.getType() == AnalysisType.METABOLIC) {
            return metabolicResultRepository.findByAnalysisRequestId(request.getId())
                    .map(mr -> {
                        var allDocs = pdfDocumentRepository
                                .findByMetabolicResultIdOrderByDocumentTypeAscVersionNumberDesc(mr.getId());
                        return requestMapper.toDtoWithMetabolic(request, metabolicMapper.toDto(mr, allDocs));
                    })
                    .orElse(requestMapper.toDto(request));
        } else {
            return stoneResultRepository.findByAnalysisRequestId(request.getId())
                    .map(sr -> requestMapper.toDtoWithStone(request, stoneResultMapper.toDto(sr)))
                    .orElse(requestMapper.toDto(request));
        }
    }
}
