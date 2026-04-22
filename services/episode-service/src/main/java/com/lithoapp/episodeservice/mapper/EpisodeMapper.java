package com.lithoapp.episodeservice.mapper;

import com.lithoapp.episodeservice.dto.request.CreateEpisodeRequest;
import com.lithoapp.episodeservice.dto.request.UpdateEpisodeRequest;
import com.lithoapp.episodeservice.dto.response.EpisodeResponse;
import com.lithoapp.episodeservice.dto.response.EpisodeSummaryResponse;
import com.lithoapp.episodeservice.entity.Episode;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EpisodeMapper {

    /**
     * Maps a creation request to an entity.
     * status is ignored here — the service always sets it to ACTIVE on creation.
     */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "status",    ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Episode toEntity(CreateEpisodeRequest request);

    EpisodeResponse toResponse(Episode episode);

    EpisodeSummaryResponse toSummary(Episode episode);

    /**
     * Applies a partial update to an existing entity.
     * Null fields in the request are ignored (NullValuePropertyMappingStrategy.IGNORE).
     * patientId and openedAt are protected from updates — use immutable case metadata.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "patientId", ignore = true)
    @Mapping(target = "openedAt",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEpisodeFromRequest(UpdateEpisodeRequest request, @MappingTarget Episode episode);
}
