package com.docify.docify_be.job.repository;

import com.docify.docify_be.job.entity.ConversionJob;
import com.docify.docify_be.job.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversionJobRepository extends JpaRepository<ConversionJob, UUID> {
    List<ConversionJob> findByStatus(JobStatus status);
    List<ConversionJob> findByExpiresAtBefore(Instant time);
    List<ConversionJob> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
