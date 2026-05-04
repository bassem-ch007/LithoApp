package com.lithoapp.drainage.notification;

import com.lithoapp.drainage.client.NotificationFeignClient;
import com.lithoapp.drainage.entity.Drainage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Best-effort publisher of drainage-related notification events.
 *
 * Recipient: the urologist who created the drainage (Drainage#doctorUsername).
 * Failures to reach notification-service are logged and swallowed.
 *
 * The scheduler runs without an HTTP request, so there is no JWT to forward.
 * In that case the Feign call goes out without an Authorization header — the
 * notification-service's event endpoint is intended for trusted internal traffic.
 * If a stricter security model is required later, switch to a service-account
 * client_credentials token here.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final NotificationFeignClient client;

    public void drainageCreated(Drainage drainage) {
        if (drainage.getDoctorUsername() == null) {
            // Without a known recipient we'd produce a noisy role-wide notification —
            // skip instead. Will be revisited if doctorUsername becomes mandatory.
            log.debug("Skipping drainageCreated notification: doctorUsername is null on drainage {}",
                    drainage.getId());
            return;
        }
        send(NotificationEventDto.builder()
                .eventType("DRAINAGE_CREATED")
                .recipientUsername(drainage.getDoctorUsername())
                .title("Drainage créé")
                .message("Drainage " + drainage.getDrainageType() + " (" + drainage.getSide()
                        + ") posé pour le patient #" + drainage.getPatientId()
                        + (drainage.getPlannedRemovalDate() != null
                                ? ", retrait prévu le " + drainage.getPlannedRemovalDate() : "") + ".")
                .referenceType("DRAINAGE")
                .referenceId(drainage.getId().toString())
                .metadata(meta(drainage))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void drainageRemovalSoon(Drainage drainage, int daysBefore) {
        if (drainage.getDoctorUsername() == null) return;
        send(NotificationEventDto.builder()
                .eventType("DRAINAGE_REMOVAL_SOON")
                .recipientUsername(drainage.getDoctorUsername())
                .title("Retrait de drainage à prévoir")
                .message("Drainage " + drainage.getDrainageType() + " (" + drainage.getSide()
                        + ") du patient #" + drainage.getPatientId()
                        + " : retrait prévu dans " + daysBefore + " jour(s) ("
                        + drainage.getPlannedRemovalDate() + ").")
                .referenceType("DRAINAGE")
                .referenceId(drainage.getId().toString())
                .metadata(meta(drainage))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void drainageOverdue(Drainage drainage) {
        if (drainage.getDoctorUsername() == null) return;
        send(NotificationEventDto.builder()
                .eventType("DRAINAGE_OVERDUE")
                .recipientUsername(drainage.getDoctorUsername())
                .title("Drainage en retard")
                .message("Le retrait du drainage " + drainage.getDrainageType()
                        + " (" + drainage.getSide() + ") du patient #" + drainage.getPatientId()
                        + " était prévu le " + drainage.getPlannedRemovalDate() + ".")
                .referenceType("DRAINAGE")
                .referenceId(drainage.getId().toString())
                .metadata(meta(drainage))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void drainageRemoved(Drainage drainage) {
        if (drainage.getDoctorUsername() == null) return;
        send(NotificationEventDto.builder()
                .eventType("DRAINAGE_REMOVED")
                .recipientUsername(drainage.getDoctorUsername())
                .title("Drainage retiré")
                .message("Drainage " + drainage.getDrainageType() + " (" + drainage.getSide()
                        + ") du patient #" + drainage.getPatientId()
                        + " marqué retiré le " + drainage.getRemovedAt() + ".")
                .referenceType("DRAINAGE")
                .referenceId(drainage.getId().toString())
                .metadata(meta(drainage))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    private void send(NotificationEventDto event) {
        try {
            client.publish(event);
            log.debug("Published drainage notification {} for drainage {}",
                    event.getEventType(), event.getReferenceId());
        } catch (Exception ex) {
            // Best-effort: a notification bus outage must not roll back drainage state
            // changes. Full stack trace lands at debug for postmortem diagnosis.
            log.warn("Failed to publish drainage notification {} for drainage {}: {}",
                    event.getEventType(), event.getReferenceId(), ex.getMessage());
            log.debug("Drainage notification publish failure detail", ex);
        }
    }

    private static Map<String, Object> meta(Drainage d) {
        Map<String, Object> m = new HashMap<>();
        m.put("episodeId", d.getEpisodeId());
        m.put("patientId", d.getPatientId());
        m.put("drainageType", d.getDrainageType() != null ? d.getDrainageType().name() : null);
        m.put("side", d.getSide() != null ? d.getSide().name() : null);
        if (d.getPlannedRemovalDate() != null) {
            m.put("plannedRemovalDate", d.getPlannedRemovalDate().toString());
        }
        return m;
    }
}
