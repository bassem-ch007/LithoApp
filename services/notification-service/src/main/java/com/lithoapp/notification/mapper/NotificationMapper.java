package com.lithoapp.notification.mapper;

import com.lithoapp.notification.dto.response.NotificationResponse;
import com.lithoapp.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .recipientUsername(n.getRecipientUsername())
                .recipientRole(n.getRecipientRole())
                .type(n.getType())
                .channel(n.getChannel())
                .status(n.getStatus())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceType(n.getReferenceType())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
