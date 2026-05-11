package com.docify.docify_be.cleanup;

import com.docify.docify_be.file.service.StorageService;
import com.docify.docify_be.job.entity.ConversionJob;
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

    // Chạy mỗi 5 phút (300000 ms)
    @Scheduled(fixedDelay = 300000)
    public void cleanupExpiredJobs() {
        log.info("Starting cleanup for expired jobs...");
        
        // Tìm các job có expires_at nhỏ hơn thời gian hiện tại
        List<ConversionJob> expiredJobs = jobRepository.findByExpiresAtBefore(Instant.now());

        int count = 0;
        for (ConversionJob job : expiredJobs) {
            try {
                // Xoá file gốc
                if (job.getSourceFileUrl() != null) {
                    storageService.delete(job.getSourceFileUrl());
                }
                
                // Xoá file kết quả
                if (job.getResultFileUrl() != null) {
                    storageService.delete(job.getResultFileUrl());
                }
                
                // Xoá record trong Database (hoặc có thể update sang EXPIRED tuỳ nghiệp vụ, 
                // nhưng tài liệu ghi xoá expired jobs nên ta xoá DB)
                jobRepository.delete(job);
                count++;
            } catch (Exception e) {
                log.error("Failed to cleanup job {}: {}", job.getId(), e.getMessage());
            }
        }
        
        if (count > 0) {
            log.info("Successfully cleaned up {} expired jobs.", count);
        } else {
            log.info("No expired jobs found.");
        }
    }
}
