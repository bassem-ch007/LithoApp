package com.lithoapp.drainage.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Outbound event sent to notification-service. Local mirror of the consumer's
 * NotificationEventDto — kept here so we don't need a shared library.
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
