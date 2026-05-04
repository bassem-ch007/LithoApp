package com.lithoapp.notification.service.channel;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.enums.NotificationChannel;

/**
 * Pluggable delivery channel.
 *
 * Each handler is responsible for actually delivering one notification (storing it,
 * sending an email, sending an SMS, …). The dispatcher fans an incoming event out
 * to every supported channel.
 */
public interface NotificationChannelHandler {

    NotificationChannel channel();

    /** Should the dispatcher invoke this handler for the given event? */
    boolean supports(NotificationEventDto event);

    /** Deliver the notification. Implementations must be idempotent on eventId. */
    void deliver(NotificationEventDto event);
}
