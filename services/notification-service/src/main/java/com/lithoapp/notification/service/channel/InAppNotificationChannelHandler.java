package com.lithoapp.notification.service.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.entity.Notification;
import com.lithoapp.notification.enums.NotificationChannel;
import com.lithoapp.notification.enums.NotificationStatus;
import com.lithoapp.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Persists the notification so authenticated users can fetch it via the
 * REST API. This is the only channel actually wired today.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InAppNotificationChannelHandler implements NotificationChannelHandler {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public boolean supports(NotificationEventDto event) {
        // IN_APP is the default — accept everything that has at least one recipient hint.
        return event.getRecipientUsername() != null
            || event.getRecipientUserId() != null
            || event.getRecipientRole() != null;
    }

    @Override
    @Transactional
    public void deliver(NotificationEventDto event) {
        Notification notification = Notification.builder()
                .recipientUserId(event.getRecipientUserId())
                .recipientUsername(event.getRecipientUsername())
                .recipientEmail(event.getRecipientEmail())
                .recipientRole(event.getRecipientRole())
                .type(event.getEventType())
                .channel(NotificationChannel.IN_APP)
                .status(NotificationStatus.UNREAD)
                .title(event.getTitle())
                .message(event.getMessage())
                .referenceType(event.getReferenceType())
                .referenceId(event.getReferenceId())
                .metadataJson(serialiseMetadata(event))
                .createdAt(event.getOccurredAt() != null ? event.getOccurredAt() : LocalDateTime.now())
                .deliveredAt(LocalDateTime.now())
                .build();

        Notification saved = repository.save(notification);
        log.info("[IN_APP] saved notification id={} type={} recipient(user={}, role={})",
                saved.getId(), saved.getType(),
                saved.getRecipientUsername(), saved.getRecipientRole());
    }

    private String serialiseMetadata(NotificationEventDto event) {
        if (event.getMetadata() == null || event.getMetadata().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(event.getMetadata());
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialise metadata for event {}: {}", event.getEventId(), e.getMessage());
            return null;
        }
    }
}
