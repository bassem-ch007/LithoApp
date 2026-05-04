package com.lithoapp.notification.controller;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal event ingestion endpoint.
 *
 * Existing services (analysis-service, drainage-service) call this endpoint via
 * Feign while forwarding the originating user's JWT. Any authenticated app user
 * may publish (the security model assumes services run inside the trusted network
 * and the JWT is forwarded for auditability, not gating).
 *
 * If a Kafka broker is wired later, the consumer will hand events to the same
 * {@link NotificationService#ingestEvent(NotificationEventDto)} method — this
 * controller stays as the synchronous transport.
 */
@RestController
@RequestMapping("/notifications/events")
@RequiredArgsConstructor
@Tag(name = "Notifications — Events",
     description = "Internal HTTP entrypoint for producing notification events")
public class NotificationEventController {

    private final NotificationService notificationService;

    @Operation(summary = "Publish a notification event (called by other services; internal-only)")
    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody NotificationEventDto event) {
        notificationService.ingestEvent(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
