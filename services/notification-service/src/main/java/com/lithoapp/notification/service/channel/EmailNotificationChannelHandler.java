package com.lithoapp.notification.service.channel;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Email channel placeholder. Disabled by default — does NOT pretend to send.
 *
 * To enable real delivery later:
 *  1. Add spring-boot-starter-mail to the pom.
 *  2. Wire JavaMailSender here.
 *  3. Flip notification.channels.email.enabled=true in config.
 */
@Slf4j
@Component
public class EmailNotificationChannelHandler implements NotificationChannelHandler {

    @Value("${notification.channels.email.enabled:false}")
    private boolean enabled;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean supports(NotificationEventDto event) {
        return enabled && event.getRecipientEmail() != null && !event.getRecipientEmail().isBlank();
    }

    @Override
    public void deliver(NotificationEventDto event) {
        // TODO(notification-service): integrate real email delivery (JavaMailSender / SES / SendGrid).
        log.debug("[EMAIL] delivery skipped — channel is a placeholder. event={} to={}",
                event.getEventType(), event.getRecipientEmail());
    }
}
