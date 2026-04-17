package com.lithoapp.analysis.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoneResultDto {

    private Long id;
    private Long analysisRequestId;

    // Morphological
    private String morphSize;
    private String morphSurface;
    private String morphColor;
    private String morphSection;
    private String morphOuterLayers;
    private String morphCore;

    // Spectrophotometry
    private String spectroSurface;
    private String spectroSection;
    private String spectroOuterLayers;
    private String spectroCore;

    // Final
    private String finalStoneType;

    // Provenance
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime createdAt;

    private Long version;
}
