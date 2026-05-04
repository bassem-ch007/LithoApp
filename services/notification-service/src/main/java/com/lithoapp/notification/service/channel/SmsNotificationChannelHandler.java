package com.lithoapp.notification.service.channel;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SMS channel placeholder. Disabled by default — does NOT pretend to send.
 *
 * To enable real delivery later: integrate an SMS gateway (Twilio, MessageBird, …)
 * and flip notification.channels.sms.enabled=true.
 */
@Slf4j
@Component
public class SmsNotificationChannelHandler implements NotificationChannelHandler {

    @Value("${notification.channels.sms.enabled:false}")
    private boolean enabled;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public boolean supports(NotificationEventDto event) {
        return enabled
                && event.getMetadata() != null
                && event.getMetadata().get("phone") instanceof String phone
                && !phone.isBlank();
    }

    @Override
    public void deliver(NotificationEventDto event) {
        // TODO(notification-service): integrate real SMS gateway.
        log.debug("[SMS] delivery skipped — channel is a placeholder. event={}", event.getEventType());
    }
}
