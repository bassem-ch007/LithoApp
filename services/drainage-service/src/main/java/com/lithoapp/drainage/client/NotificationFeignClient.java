package com.lithoapp.drainage.client;

import com.lithoapp.drainage.notification.NotificationEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification-service.base-url}")
public interface NotificationFeignClient {

    @PostMapping("/notifications/events")
    void publish(@RequestBody NotificationEventDto event);
}
