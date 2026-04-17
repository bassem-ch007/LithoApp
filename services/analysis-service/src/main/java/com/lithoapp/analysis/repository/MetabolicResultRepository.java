package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.entity.MetabolicResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetabolicResultRepository extends JpaRepository<MetabolicResult, Long> {

    Optional<MetabolicResult> findByAnalysisRequestId(Long analysisRequestId);
}
