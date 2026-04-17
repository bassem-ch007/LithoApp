package com.lithoapp.analysis.dto.response;

import com.lithoapp.analysis.domain.enums.AuditActionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditEntryDto {

    private Long id;
    private Long analysisRequestId;
    private String actorId;
    private AuditActionType actionType;
    private String targetField;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
}
