package com.lithoapp.notification.dto.response;

import com.lithoapp.notification.enums.NotificationChannel;
import com.lithoapp.notification.enums.NotificationStatus;
import com.lithoapp.notification.enums.NotificationType;
import com.lithoapp.notification.enums.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String recipientUsername;
    private String recipientRole;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String message;
    private ReferenceType referenceType;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
