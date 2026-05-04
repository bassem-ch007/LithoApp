package com.lithoapp.notification.service;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.dto.response.NotificationResponse;
import com.lithoapp.notification.entity.Notification;
import com.lithoapp.notification.enums.NotificationStatus;
import com.lithoapp.notification.exception.NotificationNotFoundException;
import com.lithoapp.notification.mapper.NotificationMapper;
import com.lithoapp.notification.repository.NotificationRepository;
import com.lithoapp.notification.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final NotificationDispatcher dispatcher;
    private final CurrentUserProvider currentUserProvider;

    // ── Producer-facing ──────────────────────────────────────────────────

    /** Entry point used by HTTP and (future) Kafka consumers. */
    public void ingestEvent(NotificationEventDto event) {
        if (event.getRecipientUsername() == null
                && event.getRecipientUserId() == null
                && event.getRecipientRole() == null) {
            throw new IllegalArgumentException(
                    "NotificationEvent must specify at least one of recipientUsername, recipientUserId, recipientRole");
        }
        if (event.getOccurredAt() == null) {
            event.setOccurredAt(LocalDateTime.now());
        }
        dispatcher.dispatch(event);
    }

    // ── User-facing reads ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificationResponse> listForCurrentUser(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(Math.min(size, 100), 1));
        String username = currentUserProvider.getUsername();
        String role = primaryRoleOrNull();
        return repository.findVisibleToUser(username, role, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long countUnreadForCurrentUser() {
        String username = currentUserProvider.getUsername();
        String role = primaryRoleOrNull();
        return repository.countByVisibilityAndStatus(username, role, NotificationStatus.UNREAD);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id) {
        Notification n = loadOrThrow(id);
        ensureReadAccess(n);
        return mapper.toResponse(n);
    }

    // ── User-facing writes ───────────────────────────────────────────────

    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        Notification n = loadOrThrow(id);
        ensureReadAccess(n);

        if (n.getStatus() != NotificationStatus.READ) {
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(LocalDateTime.now());
            n = repository.save(n);
        }
        return mapper.toResponse(n);
    }

    @Transactional
    public int markAllAsReadForCurrentUser() {
        String username = currentUserProvider.getUsername();
        String role = primaryRoleOrNull();
        int updated = repository.markAllAsRead(username, role);
        log.info("Marked {} notifications as read for user={} role={}", updated, username, role);
        return updated;
    }

    // ── Admin ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificationResponse> listAllForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(Math.min(size, 200), 1));
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private Notification loadOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    }

    private void ensureReadAccess(Notification n) {
        if (currentUserProvider.isAdmin()) return;

        String username = currentUserProvider.getUsername();
        if (username != null && username.equals(n.getRecipientUsername())) return;

        if (n.getRecipientUsername() == null
                && n.getRecipientRole() != null
                && currentUserProvider.getAppRoles().contains(n.getRecipientRole())) {
            return;
        }
        throw new AccessDeniedException("Notification " + n.getId() + " is not visible to the current user");
    }

    /**
     * Pick a role to use for role-wide filtering. We use the first non-ADMIN
     * app role since UROLOGUE/BIOLOGIST are mutually exclusive in this app.
     * Returns null when the user has no role (no role-wide filtering applies).
     */
    private String primaryRoleOrNull() {
        List<String> ordered = List.of("UROLOGUE", "BIOLOGIST", "ADMIN");
        var roles = currentUserProvider.getAppRoles();
        for (String r : ordered) {
            if (roles.contains(r)) return r;
        }
        return null;
    }
}
