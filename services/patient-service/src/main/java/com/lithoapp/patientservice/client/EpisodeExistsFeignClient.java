package com.lithoapp.patientservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "episode-service", url = "${episode-service.base-url}")
public interface EpisodeExistsFeignClient {

    @GetMapping("/episodes/patient/{patientId}/exists")
    boolean hasEpisodes(@PathVariable("patientId") Long patientId);
}
