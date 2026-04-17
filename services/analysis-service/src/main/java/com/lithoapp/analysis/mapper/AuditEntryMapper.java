package com.lithoapp.analysis.mapper;

import com.lithoapp.analysis.dto.response.AuditEntryDto;
import com.lithoapp.analysis.entity.AuditEntry;
import org.springframework.stereotype.Component;

@Component
public class AuditEntryMapper {

    public AuditEntryDto toDto(AuditEntry entity) {
        AuditEntryDto dto = new AuditEntryDto();
        dto.setId(entity.getId());
        dto.setAnalysisRequestId(entity.getAnalysisRequestId());
        dto.setActorId(entity.getActorId());
        dto.setActionType(entity.getActionType());
        dto.setTargetField(entity.getTargetField());
        dto.setOldValue(entity.getOldValue());
        dto.setNewValue(entity.getNewValue());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}
