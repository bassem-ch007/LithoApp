package com.lithoapp.episodeservice.client;

import com.lithoapp.episodeservice.dto.client.PatientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${patient-service.base-url}")
public interface PatientFeignClient {

    @GetMapping("/patients/{id}")
    PatientResponse getById(@PathVariable("id") Long id);
}
