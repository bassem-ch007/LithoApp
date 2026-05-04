package com.lithoapp.notification.controller;

import com.lithoapp.notification.dto.response.NotificationResponse;
import com.lithoapp.notification.dto.response.UnreadCountResponse;
import com.lithoapp.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications",
     description = "User-facing in-app notifications (list / mark read / count)")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "List current user's notifications (paginated, newest first)")
    @GetMapping
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<Page<NotificationResponse>> list(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, max 100")    @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(notificationService.listForCurrentUser(page, size));
    }

    @Operation(summary = "Number of unread notifications for the current user")
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<UnreadCountResponse> unreadCount() {
        return ResponseEntity.ok(new UnreadCountResponse(notificationService.countUnreadForCurrentUser()));
    }

    @Operation(summary = "Get a notification by ID (must be visible to current user)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "Mark all current user notifications as read")
    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('UROLOGUE', 'BIOLOGIST', 'ADMIN')")
    public ResponseEntity<UnreadCountResponse> markAllAsRead() {
        int updated = notificationService.markAllAsReadForCurrentUser();
        return ResponseEntity.ok(new UnreadCountResponse(updated));
    }

    @Operation(summary = "ADMIN — list all notifications across users")
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<NotificationResponse>> adminList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(notificationService.listAllForAdmin(page, size));
    }
}
