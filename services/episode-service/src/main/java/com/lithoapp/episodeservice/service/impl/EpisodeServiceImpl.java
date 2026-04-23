package com.lithoapp.episodeservice.service.impl;

import com.lithoapp.episodeservice.dto.request.CreateEpisodeRequest;
import com.lithoapp.episodeservice.dto.request.UpdateEpisodeRequest;
import com.lithoapp.episodeservice.dto.response.EpisodeResponse;
import com.lithoapp.episodeservice.dto.response.EpisodeSummaryResponse;
import com.lithoapp.episodeservice.entity.Episode;
import com.lithoapp.episodeservice.enums.EpisodeStatus;
import com.lithoapp.episodeservice.exception.EpisodeNotFoundException;
import com.lithoapp.episodeservice.mapper.EpisodeMapper;
import com.lithoapp.episodeservice.repository.EpisodeRepository;
import com.lithoapp.episodeservice.service.EpisodeService;
import com.lithoapp.episodeservice.service.PatientValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeMapper episodeMapper;
    private final PatientValidationService patientValidationService;

    @Override
    public EpisodeResponse createEpisode(CreateEpisodeRequest request) {
        log.info("Opening new episode for patientId={}", request.getPatientId());

        patientValidationService.validatePatientExists(request.getPatientId());

        Episode episode = episodeMapper.toEntity(request);
        episode.setStatus(EpisodeStatus.ACTIVE);

        Episode saved = episodeRepository.save(episode);
        log.info("Episode created — id={}, patientId={}", saved.getId(), saved.getPatientId());
        return episodeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EpisodeResponse getEpisodeById(Long id) {
        return episodeMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EpisodeSummaryResponse> getEpisodesByPatient(Long patientId, EpisodeStatus status, Pageable pageable) {
        Page<Episode> episodes = (status != null)
                ? episodeRepository.findByPatientIdAndStatus(patientId, status, pageable)
                : episodeRepository.findByPatientId(patientId, pageable);
        return episodes.map(episodeMapper::toSummary);
    }

    @Override
    public EpisodeResponse updateEpisode(Long id, UpdateEpisodeRequest request) {
        Episode episode = findOrThrow(id);
        episodeMapper.updateEpisodeFromRequest(request, episode);
        Episode saved = episodeRepository.save(episode);
        log.info("Episode updated — id={}", id);
        return episodeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEpisodes(Long patientId) {
        return episodeRepository.countByPatientId(patientId) > 0;
    }

    private Episode findOrThrow(Long id) {
        return episodeRepository.findById(id)
                .orElseThrow(() -> new EpisodeNotFoundException(id));
    }
}
