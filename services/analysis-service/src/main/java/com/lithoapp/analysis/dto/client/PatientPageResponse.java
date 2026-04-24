package com.lithoapp.analysis.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Wrapper mirroring Spring's {@code PageImpl} JSON shape so Jackson can
 * deserialize responses from {@code GET /patients/search} via Feign.
 *
 * Only the {@code content} array is consumed here — paging metadata
 * (totalElements, pageable, sort, etc.) is intentionally ignored.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientPageResponse {

    private List<PatientResponse> content;
}
