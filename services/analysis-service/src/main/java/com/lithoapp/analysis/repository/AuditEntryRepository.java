package com.lithoapp.analysis.repository;

import com.lithoapp.analysis.entity.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {

    List<AuditEntry> findByAnalysisRequestIdOrderByTimestampAsc(Long analysisRequestId);
}
