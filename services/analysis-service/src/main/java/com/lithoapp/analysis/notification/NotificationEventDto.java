package com.lithoapp.analysis.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Outbound event sent to notification-service.
 *
 * Mirrors {@code com.lithoapp.notification.dto.event.NotificationEventDto} on
 * the consumer side. Kept as a local copy to avoid a shared-library dependency.
 *
 * One of {@code recipientUsername} or {@code recipientRole} should be set.
 * {@code eventType} matches the consumer's {@code NotificationType} enum:
 * ANALYSIS_CREATED, ANALYSIS_STARTED, ANALYSIS_RESULT_ADDED, ANALYSIS_COMPLETED,
 * DRAINAGE_*, SYSTEM.
 *
 * referenceType is one of: PATIENT, EPISODE, ANALYSIS, DRAINAGE, SYSTEM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDto {
    private String eventId;
    private String eventType;
    private String recipientUserId;
    private String recipientUsername;
    private String recipientEmail;
    private String recipientRole;
    private String title;
    private String message;
    private String referenceType;
    private String referenceId;
    private Map<String, Object> metadata;
    private LocalDateTime occurredAt;
}
