package com.lithoapp.analysis.notification;

import com.lithoapp.analysis.client.NotificationFeignClient;
import com.lithoapp.analysis.entity.AnalysisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Best-effort publisher of analysis-related notification events.
 *
 * Failures to reach notification-service are logged and swallowed — a flaky
 * notification pipeline must not break the clinical workflow.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final NotificationFeignClient client;

    public void analysisCreated(AnalysisRequest request, String actor) {
        // Recipient: BIOLOGIST role-wide. The system has no assigned biologist field today.
        // TODO(notification): switch to a targeted biologist when an assignment field is added.
        String typeLabel = request.getType().name().toLowerCase();
        send(NotificationEventDto.builder()
                .eventType("ANALYSIS_CREATED")
                .recipientRole("BIOLOGIST")
                .title("Nouvelle demande d'analyse")
                .message("Nouvelle demande d'analyse " + typeLabel
                        + " (#" + request.getId() + ") créée par " + actor + ".")
                .referenceType("ANALYSIS")
                .referenceId(String.valueOf(request.getId()))
                .metadata(meta(
                        "patientId", request.getPatientId(),
                        "episodeId", request.getEpisodeId(),
                        "analysisType", request.getType().name()))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void analysisStarted(AnalysisRequest request, String biologist) {
        // Recipient: the urologist who created the request.
        send(NotificationEventDto.builder()
                .eventType("ANALYSIS_STARTED")
                .recipientUsername(request.getCreatedBy())
                .title("Analyse prise en charge")
                .message("Le biologiste " + biologist
                        + " a commencé à travailler sur votre demande d'analyse #"
                        + request.getId() + ".")
                .referenceType("ANALYSIS")
                .referenceId(String.valueOf(request.getId()))
                .metadata(meta(
                        "biologist", biologist,
                        "analysisType", request.getType().name()))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void analysisResultAdded(AnalysisRequest request, String biologist, String detail) {
        send(NotificationEventDto.builder()
                .eventType("ANALYSIS_RESULT_ADDED")
                .recipientUsername(request.getCreatedBy())
                .title("Nouveau résultat d'analyse")
                .message("Un nouveau résultat a été ajouté à votre demande d'analyse #"
                        + request.getId() + (detail != null ? " (" + detail + ")" : "") + ".")
                .referenceType("ANALYSIS")
                .referenceId(String.valueOf(request.getId()))
                .metadata(meta(
                        "biologist", biologist,
                        "analysisType", request.getType().name(),
                        "detail", detail))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    public void analysisCompleted(AnalysisRequest request, String biologist) {
        send(NotificationEventDto.builder()
                .eventType("ANALYSIS_COMPLETED")
                .recipientUsername(request.getCreatedBy())
                .title("Analyse terminée")
                .message("Votre demande d'analyse #" + request.getId() + " a été clôturée.")
                .referenceType("ANALYSIS")
                .referenceId(String.valueOf(request.getId()))
                .metadata(meta(
                        "biologist", biologist,
                        "analysisType", request.getType().name()))
                .occurredAt(LocalDateTime.now())
                .build());
    }

    private void send(NotificationEventDto event) {
        try {
            client.publish(event);
            log.debug("Published notification event {} (ref={}, recipient={}, role={})",
                    event.getEventType(), event.getReferenceId(),
                    event.getRecipientUsername(), event.getRecipientRole());
        } catch (Exception ex) {
            // Notification delivery is best-effort — we never roll back a domain action
            // because the notification bus is down. Trace at debug for full diagnosis.
            log.warn("Failed to publish notification event {} (ref={}, recipient={}, role={}): {}",
                    event.getEventType(), event.getReferenceId(),
                    event.getRecipientUsername(), event.getRecipientRole(), ex.getMessage());
            log.debug("Notification publish failure detail", ex);
        }
    }

    private static Map<String, Object> meta(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            if (kv[i] instanceof String key && kv[i + 1] != null) {
                m.put(key, kv[i + 1]);
            }
        }
        return m;
    }
}
