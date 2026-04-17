package com.lithoapp.analysis.mapper;

import com.lithoapp.analysis.dto.response.StoneResultDto;
import com.lithoapp.analysis.entity.StoneResult;
import org.springframework.stereotype.Component;

@Component
public class StoneResultMapper {

    public StoneResultDto toDto(StoneResult entity) {
        StoneResultDto dto = new StoneResultDto();
        dto.setId(entity.getId());
        dto.setAnalysisRequestId(entity.getAnalysisRequestId());
        dto.setMorphSize(entity.getMorphSize());
        dto.setMorphSurface(entity.getMorphSurface());
        dto.setMorphColor(entity.getMorphColor());
        dto.setMorphSection(entity.getMorphSection());
        dto.setMorphOuterLayers(entity.getMorphOuterLayers());
        dto.setMorphCore(entity.getMorphCore());
        dto.setSpectroSurface(entity.getSpectroSurface());
        dto.setSpectroSection(entity.getSpectroSection());
        dto.setSpectroOuterLayers(entity.getSpectroOuterLayers());
        dto.setSpectroCore(entity.getSpectroCore());
        dto.setFinalStoneType(entity.getFinalStoneType());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedAt(entity.getLastModifiedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setVersion(entity.getVersion());
        return dto;
    }
}
