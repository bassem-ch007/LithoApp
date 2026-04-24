package com.lithoapp.analysis.client;

import com.lithoapp.analysis.dto.client.PatientPageResponse;
import com.lithoapp.analysis.dto.client.PatientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "patient-service", url = "${patient-service.base-url}")
public interface PatientFeignClient {

    @GetMapping("/patients/{id}")
    PatientResponse getById(@PathVariable("id") Long id);

    /**
     * Patient identity search — mirrors
     * {@code GET /patients/search?di=&dmi=&name=&phone=} on patient-service.
     *
     * All params are optional (partial match). The remote endpoint returns a
     * Spring {@code Page}; only the {@code content} array is consumed here.
     */
    @GetMapping("/patients/search")
    PatientPageResponse search(@RequestParam(value = "di",    required = false) String di,
                               @RequestParam(value = "dmi",   required = false) String dmi,
                               @RequestParam(value = "name",  required = false) String name,
                               @RequestParam(value = "phone", required = false) String phone,
                               @RequestParam(value = "size",  required = false) Integer size);
}
