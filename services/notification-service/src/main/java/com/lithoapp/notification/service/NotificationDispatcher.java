package com.lithoapp.notification.service;

import com.lithoapp.notification.dto.event.NotificationEventDto;
import com.lithoapp.notification.service.channel.NotificationChannelHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fans an incoming event out to every supporting channel handler.
 *
 * Today only the IN_APP handler is wired. Email/SMS handlers are disabled
 * placeholders. Adding a new channel = adding a new {@link NotificationChannelHandler}
 * bean — no other class needs to change.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final List<NotificationChannelHandler> handlers;

    public void dispatch(NotificationEventDto event) {
        event.getOrAssignEventId();
        log.debug("Dispatching notification event id={} type={} ({} handlers registered)",
                event.getEventId(), event.getEventType(), handlers.size());

        for (NotificationChannelHandler handler : handlers) {
            if (!handler.supports(event)) {
                continue;
            }
            try {
                handler.deliver(event);
            } catch (Exception ex) {
                // One failing channel must not poison the others.
                log.error("Channel {} failed to deliver event {}: {}",
                        handler.channel(), event.getEventId(), ex.getMessage(), ex);
            }
        }
    }
}
