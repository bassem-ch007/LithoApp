package com.lithoapp.analysis.service;

import com.lithoapp.analysis.domain.enums.AuditActionType;
import com.lithoapp.analysis.dto.response.AuditEntryDto;
import com.lithoapp.analysis.entity.AuditEntry;
import com.lithoapp.analysis.mapper.AuditEntryMapper;
import com.lithoapp.analysis.repository.AuditEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Internal service responsible for writing audit entries.
 *
 * This service uses default transaction propagation (REQUIRED), meaning it always
 * joins the caller's transaction. A rolled-back operation will also roll back
 * the audit entry — there are no dangling audit records for failed operations.
 *
 * Callers should invoke {@link #record} after confirming the operation succeeded
 * but before the transaction commits (i.e. still inside the same @Transactional boundary).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEntryRepository auditEntryRepository;
    private final AuditEntryMapper auditEntryMapper;

    /**
     * Record a single audit event.
     *
     * @param requestId   the affected AnalysisRequest id
     * @param actorId     the user performing the action
     * @param actionType  semantic action type
     * @param targetField the field or resource affected (null for coarse-grained events)
     * @param oldValue    the previous value (null if not applicable)
     * @param newValue    the new value (null if not applicable)
     */
    @Transactional
    public void record(Long requestId, String actorId, AuditActionType actionType,
                       String targetField, String oldValue, String newValue) {
        AuditEntry entry = new AuditEntry();
        entry.setAnalysisRequestId(requestId);
        entry.setActorId(actorId);
        entry.setActionType(actionType);
        entry.setTargetField(targetField);
        entry.setOldValue(oldValue);
        entry.setNewValue(newValue);
        entry.setTimestamp(LocalDateTime.now());
        auditEntryRepository.save(entry);
        log.debug("Audit [request={}] {} on '{}': {} → {}",
                requestId, actionType, targetField, oldValue, newValue);
    }

    @Transactional(readOnly = true)
    public List<AuditEntryDto> getAuditLog(Long requestId) {
        return auditEntryRepository
                .findByAnalysisRequestIdOrderByTimestampAsc(requestId)
                .stream()
                .map(auditEntryMapper::toDto)
                .toList();
    }
}
