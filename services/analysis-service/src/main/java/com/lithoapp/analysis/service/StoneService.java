package com.lithoapp.analysis.service;

import com.lithoapp.analysis.domain.enums.AnalysisType;
import com.lithoapp.analysis.domain.enums.AuditActionType;
import com.lithoapp.analysis.dto.request.UpdateStoneResultDto;
import com.lithoapp.analysis.dto.response.StoneResultDto;
import com.lithoapp.analysis.entity.AnalysisRequest;
import com.lithoapp.analysis.entity.StoneResult;
import com.lithoapp.analysis.exception.AnalysisNotFoundException;
import com.lithoapp.analysis.exception.InvalidAnalysisTypeOperationException;
import com.lithoapp.analysis.mapper.StoneResultMapper;
import com.lithoapp.analysis.repository.StoneResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoneService {

    private final AnalysisRequestService requestService;
    private final StoneResultRepository stoneResultRepository;
    private final AuditService auditService;
    private final StoneResultMapper stoneResultMapper;

    // ── Update ────────────────────────────────────────────────────────────

    /**
     * Partially update the stone result.
     *
     * Only non-null fields in the DTO are applied. For each changed field,
     * an individual STONE_RESULT_FIELD_UPDATED audit entry is written so
     * the audit log answers "who changed morphSize from X to Y at time T".
     *
     * @Version on StoneResult ensures concurrent updates from two biologists
     * are detected: the second save throws ObjectOptimisticLockingFailureException,
     * which the GlobalExceptionHandler maps to HTTP 409.
     */
    @Transactional
    public StoneResultDto updateStoneResult(Long requestId, UpdateStoneResultDto dto, String actor) {
        AnalysisRequest request = requestService.loadOrThrow(requestId);
        request.guardNotCompleted();

        if (request.getType() != AnalysisType.STONE) {
            throw new InvalidAnalysisTypeOperationException(
                    "Cannot update stone result: request " + requestId + " is of type " + request.getType());
        }

        StoneResult stone = stoneResultRepository.findByAnalysisRequestId(requestId)
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));

        boolean isFirstUpdate = (stone.getLastModifiedBy() == null);

        // ── Apply partial update, audit each changed field ────────────────
        applyIfChanged(stone, dto.getMorphSize(),       StoneResult::getMorphSize,       StoneResult::setMorphSize,       "morphSize",        requestId, actor);
        applyIfChanged(stone, dto.getMorphSurface(),    StoneResult::getMorphSurface,    StoneResult::setMorphSurface,    "morphSurface",     requestId, actor);
        applyIfChanged(stone, dto.getMorphColor(),      StoneResult::getMorphColor,      StoneResult::setMorphColor,      "morphColor",       requestId, actor);
        applyIfChanged(stone, dto.getMorphSection(),    StoneResult::getMorphSection,    StoneResult::setMorphSection,    "morphSection",     requestId, actor);
        applyIfChanged(stone, dto.getMorphOuterLayers(),StoneResult::getMorphOuterLayers,StoneResult::setMorphOuterLayers,"morphOuterLayers", requestId, actor);
        applyIfChanged(stone, dto.getMorphCore(),       StoneResult::getMorphCore,       StoneResult::setMorphCore,       "morphCore",        requestId, actor);
        applyIfChanged(stone, dto.getSpectroSurface(),  StoneResult::getSpectroSurface,  StoneResult::setSpectroSurface,  "spectroSurface",   requestId, actor);
        applyIfChanged(stone, dto.getSpectroSection(),  StoneResult::getSpectroSection,  StoneResult::setSpectroSection,  "spectroSection",   requestId, actor);
        applyIfChanged(stone, dto.getSpectroOuterLayers(),StoneResult::getSpectroOuterLayers,StoneResult::setSpectroOuterLayers,"spectroOuterLayers",requestId, actor);
        applyIfChanged(stone, dto.getSpectroCore(),     StoneResult::getSpectroCore,     StoneResult::setSpectroCore,     "spectroCore",      requestId, actor);
        applyIfChanged(stone, dto.getFinalStoneType(),  StoneResult::getFinalStoneType,  StoneResult::setFinalStoneType,  "finalStoneType",   requestId, actor);

        stone.setLastModifiedBy(actor);
        stone.setLastModifiedAt(LocalDateTime.now());
        stoneResultRepository.save(stone);

        // Coarse-grained audit for first save
        if (isFirstUpdate) {
            auditService.record(requestId, actor,
                    AuditActionType.STONE_RESULT_CREATED, null, null, null);
        }

        // ── Auto-transition CREATED → IN_PROGRESS ─────────────────────────
        requestService.transitionToInProgressIfNeeded(request, actor);

        return stoneResultMapper.toDto(stone);
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StoneResultDto getStoneResult(Long requestId) {
        AnalysisRequest request = requestService.loadOrThrow(requestId);
        if (request.getType() != AnalysisType.STONE) {
            throw new InvalidAnalysisTypeOperationException(
                    "Request " + requestId + " is not of type STONE");
        }
        StoneResult stone = stoneResultRepository.findByAnalysisRequestId(requestId)
                .orElseThrow(() -> new AnalysisNotFoundException(requestId));
        return stoneResultMapper.toDto(stone);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Apply a field update only if the DTO value is non-null AND different from
     * the current value. Writes one audit entry per changed field.
     */
    private void applyIfChanged(StoneResult stone,
                                String newValue,
                                Function<StoneResult, String> getter,
                                BiConsumer<StoneResult, String> setter,
                                String fieldName,
                                Long requestId,
                                String actorId) {
        if (newValue == null) return;  // not provided in this partial update

        String currentValue = getter.apply(stone);
        if (!newValue.equals(currentValue)) {
            setter.accept(stone, newValue);
            auditService.record(requestId, actorId,
                    AuditActionType.STONE_RESULT_FIELD_UPDATED, fieldName,
                    currentValue, newValue);
        }
    }
}
