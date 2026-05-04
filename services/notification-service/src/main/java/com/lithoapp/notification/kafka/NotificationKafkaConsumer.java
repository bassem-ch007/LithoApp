package com.lithoapp.notification.kafka;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that forwards events into the same {@link NotificationService}
 * pipeline used by the HTTP endpoint.
 *
 * Disabled by default — only instantiated when notification.kafka.enabled=true
 * AND the spring-kafka starter is on the classpath. The pom marks spring-kafka
 * as optional so deployments without a broker stay slim.
 *
 * Required additions to docker-compose to enable end-to-end Kafka delivery:
 *   - a Kafka broker (e.g. bitnami/kafka)
 *   - KAFKA_BOOTSTRAP_SERVERS=kafka:9092 on each producer + this service
 *   - NOTIFICATION_KAFKA_ENABLED=true on this service
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "notification.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${notification.kafka.topics.events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onEvent(NotificationEventDto event) {
        try {
            log.debug("[Kafka] received notification event id={} type={}",
                    event.getEventId(), event.getEventType());
            notificationService.ingestEvent(event);
        } catch (Exception ex) {
            log.error("[Kafka] failed to process event {}: {}", event.getEventId(), ex.getMessage(), ex);
        }
    }
}
