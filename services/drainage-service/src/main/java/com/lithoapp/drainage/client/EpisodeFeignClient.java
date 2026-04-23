package com.lithoapp.drainage.client;

import com.lithoapp.drainage.dto.client.EpisodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "episode-service", url = "${episode-service.base-url}")
public interface EpisodeFeignClient {

    @GetMapping("/episodes/{id}")
    EpisodeResponse getById(@PathVariable("id") Long id);
}
