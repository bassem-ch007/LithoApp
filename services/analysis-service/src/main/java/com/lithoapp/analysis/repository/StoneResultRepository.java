package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.entity.StoneResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoneResultRepository extends JpaRepository<StoneResult, Long> {

    Optional<StoneResult> findByAnalysisRequestId(Long analysisRequestId);
}
