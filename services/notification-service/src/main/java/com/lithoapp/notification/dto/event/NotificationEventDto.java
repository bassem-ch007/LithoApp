package com.lithoapp.notification.dto.event;

import com.lithoapp.notification.enums.NotificationType;
import com.lithoapp.notification.enums.ReferenceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event consumed by notification-service.
 *
 * Producers fill in either {@code recipientUsername} (targeted) or {@code recipientRole}
 * (role-wide). At least one must be present.
 *
 * Same shape is used regardless of transport — HTTP today, Kafka tomorrow.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDto {

    /** Idempotency key — producers may set this. Defaults to a server-side UUID. */
    private String eventId;

    @NotNull
    private NotificationType eventType;

    private String recipientUserId;
    private String recipientUsername;
    private String recipientEmail;
    /** "UROLOGUE", "BIOLOGIST", "ADMIN" */
    private String recipientRole;

    @NotNull
    private String title;

    @NotNull
    private String message;

    private ReferenceType referenceType;
    private String referenceId;

    /** Free-form key/value metadata, serialised as JSON in storage. */
    private Map<String, Object> metadata;

    private LocalDateTime occurredAt;

    public String getOrAssignEventId() {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        return eventId;
    }
}
