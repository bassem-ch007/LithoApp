package com.lithoapp.notification.entity;

import com.lithoapp.notification.enums.NotificationChannel;
import com.lithoapp.notification.enums.NotificationStatus;
import com.lithoapp.notification.enums.NotificationType;
import com.lithoapp.notification.enums.ReferenceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * In-app notification.
 *
 * Recipient resolution:
 *  - Targeted notification: recipientUsername is set, recipientRole may also be set.
 *  - Role-wide notification: recipientUsername is null, recipientRole is set
 *    (the user query unions personal + role-wide notifications for the caller).
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notif_recipient_user", columnList = "recipient_username"),
                @Index(name = "idx_notif_recipient_role", columnList = "recipient_role"),
                @Index(name = "idx_notif_status",         columnList = "status"),
                @Index(name = "idx_notif_reference",      columnList = "reference_type, reference_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Keycloak subject (UUID) of the recipient when known. May be null for role-wide. */
    @Column(name = "recipient_user_id")
    private String recipientUserId;

    /** Keycloak preferred_username of the recipient. Null for role-wide notifications. */
    @Column(name = "recipient_username", length = 128)
    private String recipientUsername;

    /** Cached email at the time the notification was created (used by future EMAIL channel). */
    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    /** Recipient role (UROLOGUE, BIOLOGIST, ADMIN). Set when the notification targets a role. */
    @Column(name = "recipient_role", length = 32)
    private String recipientRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 64)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private NotificationStatus status;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 32)
    private ReferenceType referenceType;

    @Column(name = "reference_id", length = 64)
    private String referenceId;

    /** Free-form JSON metadata serialised as a string. */
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
