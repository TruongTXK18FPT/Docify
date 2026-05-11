package com.docify.docify_be.queue;

import com.docify.docify_be.common.config.RabbitMQConfig;
import com.docify.docify_be.conversion.engine.ConversionEngine;
import com.docify.docify_be.file.service.StorageService;
import com.docify.docify_be.job.entity.ConversionJob;
import com.docify.docify_be.job.enums.JobStatus;
import com.docify.docify_be.job.repository.ConversionJobRepository;
import com.docify.docify_be.queue.message.ConversionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionWorker {

    private final ConversionJobRepository jobRepository;
    private final StorageService storageService;
    private final List<ConversionEngine> engines;

    @RabbitListener(queues = RabbitMQConfig.CONVERSION_QUEUE)
    @Transactional
    public void processConversion(ConversionMessage message) {
        log.info("Received conversion job: {}", message.getJobId());

        ConversionJob job = jobRepository.findById(message.getJobId()).orElse(null);
        if (job == null) {
            log.error("Job {} not found in database.", message.getJobId());
            return;
        }

        try {
            job.setStatus(JobStatus.PROCESSING);
            job.setProgress(10);
            jobRepository.saveAndFlush(job);

            ConversionEngine activeEngine = null;
            for (ConversionEngine engine : engines) {
                if (engine.supports(job.getSourceType(), job.getTargetType())) {
                    activeEngine = engine;
                    break;
                }
            }

            if (activeEngine == null) {
                throw new IllegalStateException("No suitable engine found for " + job.getSourceType() + " -> " + job.getTargetType());
            }

            // Read source file from storage
            job.setProgress(20);
            jobRepository.saveAndFlush(job);
            
            try (InputStream sourceStream = storageService.read(job.getSourceFileUrl())) {
                job.setProgress(50);
                jobRepository.saveAndFlush(job);

                // Convert
                try (InputStream resultStream = activeEngine.convert(sourceStream, job.getOriginalFileName())) {
                    job.setProgress(80);
                    jobRepository.saveAndFlush(job);

                    // Save result back to storage
                    String resultExt = job.getTargetType();
                    String resultUrl = storageService.save(resultStream, "output." + resultExt);

                    job.setResultFileUrl(resultUrl);
                    job.setProgress(100);
                    job.setStatus(JobStatus.COMPLETED);
                    job.setCompletedAt(Instant.now());
                    job.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS)); // Default 1 hour TTL
                }
            }
        } catch (Exception e) {
            log.error("Failed to process job {}: {}", message.getJobId(), e.getMessage());
            job.setStatus(JobStatus.FAILED);
            job.setErrorCode("CONVERSION_FAILED");
            job.setErrorMessage(e.getMessage());
        } finally {
            jobRepository.saveAndFlush(job);
        }
    }
}
