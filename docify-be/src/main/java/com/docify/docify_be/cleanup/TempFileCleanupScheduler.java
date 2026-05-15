package com.docify.docify_be.cleanup;

import com.docify.docify_be.file.service.StorageService;
import com.docify.docify_be.job.entity.ConversionJob;
import com.docify.docify_be.job.enums.JobStatus;
import com.docify.docify_be.job.repository.ConversionJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempFileCleanupScheduler {

    private final ConversionJobRepository jobRepository;
    private final StorageService storageService;

    @Scheduled(fixedDelayString = "${docify.cleanup.fixed-delay-ms:300000}")
    public void cleanupExpiredJobs() {
        log.info("Starting cleanup for expired jobs...");

        List<ConversionJob> expiredJobs = jobRepository.findByExpiresAtBefore(Instant.now()).stream()
                .filter(job -> job.getStatus() != JobStatus.EXPIRED)
                .toList();

        int count = 0;
        for (ConversionJob job : expiredJobs) {
            try {
                storageService.delete(job.getSourceFileUrl());
                storageService.delete(job.getResultFileUrl());

                job.setStatus(JobStatus.EXPIRED);
                job.setSourceFileUrl(null);
                job.setResultFileUrl(null);
                jobRepository.save(job);
                count++;
            } catch (Exception e) {
                log.error("Failed to cleanup job {}: {}", job.getId(), e.getMessage());
            }
        }

        log.info("Expired job cleanup finished. count={}", count);
    }
}
