package com.lithoapp.analysis.mapper;

import com.lithoapp.analysis.dto.response.AnalysisRequestDto;
import com.lithoapp.analysis.dto.response.MetabolicResultDto;
import com.lithoapp.analysis.dto.response.StoneResultDto;
import com.lithoapp.analysis.entity.AnalysisRequest;
import org.springframework.stereotype.Component;

@Component
public class AnalysisRequestMapper {

    public AnalysisRequestDto toDto(AnalysisRequest entity) {
        AnalysisRequestDto dto = new AnalysisRequestDto();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatientId());
        dto.setEpisodeId(entity.getEpisodeId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setCompletedBy(entity.getCompletedBy());
        dto.setVersion(entity.getVersion());
        return dto;
    }

    public AnalysisRequestDto toDtoWithMetabolic(AnalysisRequest entity, MetabolicResultDto metabolicDto) {
        AnalysisRequestDto dto = toDto(entity);
        dto.setMetabolicResult(metabolicDto);
        return dto;
    }

    public AnalysisRequestDto toDtoWithStone(AnalysisRequest entity, StoneResultDto stoneDto) {
        AnalysisRequestDto dto = toDto(entity);
        dto.setStoneResult(stoneDto);
        return dto;
    }
}
