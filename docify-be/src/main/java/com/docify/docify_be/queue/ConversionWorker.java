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

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionWorker {

    private static final EnumSet<JobStatus> TERMINAL_STATUSES = EnumSet.of(
            JobStatus.COMPLETED,
            JobStatus.CANCELLED,
            JobStatus.EXPIRED
    );

    private final ConversionJobRepository jobRepository;
    private final StorageService storageService;
    private final List<ConversionEngine> engines;

    @RabbitListener(queues = RabbitMQConfig.CONVERSION_QUEUE)
    @Transactional(noRollbackFor = Exception.class)
    public void processConversion(ConversionMessage message) {
        Instant startedAt = Instant.now();
        log.info("jobId={} status=RECEIVED source={} target={}", message.getJobId(), message.getSourceType(), message.getTargetType());

        ConversionJob job = jobRepository.findById(message.getJobId()).orElse(null);
        if (job == null) {
            log.warn("jobId={} status=SKIPPED reason=JOB_NOT_FOUND", message.getJobId());
            return;
        }
        if (TERMINAL_STATUSES.contains(job.getStatus())) {
            log.info("jobId={} status=SKIPPED currentStatus={}", job.getId(), job.getStatus());
            return;
        }

        try {
            job.setStatus(JobStatus.PROCESSING);
            job.setProgress(10);
            jobRepository.saveAndFlush(job);

            ConversionEngine activeEngine = engines.stream()
                    .filter(engine -> engine.supports(job.getSourceType(), job.getTargetType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No suitable engine found for " + job.getSourceType() + " -> " + job.getTargetType()));

            job.setProgress(30);
            jobRepository.saveAndFlush(job);

            try (InputStream sourceStream = storageService.read(job.getSourceFileUrl())) {
                job.setProgress(70);
                jobRepository.saveAndFlush(job);

                try (InputStream resultStream = activeEngine.convert(
                        sourceStream,
                        job.getSourceType(),
                        job.getTargetType(),
                        job.getOriginalFileName())) {
                    job.setProgress(90);
                    jobRepository.saveAndFlush(job);

                    String resultUrl = storageService.saveResult(resultStream, "output." + job.getTargetType());

                    job.setResultFileUrl(resultUrl);
                    job.setProgress(100);
                    job.setStatus(JobStatus.COMPLETED);
                    job.setCompletedAt(Instant.now());
                    job.setErrorCode(null);
                    job.setErrorMessage(null);
                }
            }

            log.info("jobId={} status=COMPLETED engine={} durationMs={}",
                    job.getId(),
                    activeEngine.getClass().getSimpleName(),
                    Duration.between(startedAt, Instant.now()).toMillis());
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setProgress(0);
            job.setErrorCode("CONVERSION_FAILED");
            job.setErrorMessage(e.getMessage());
            log.error("jobId={} status=FAILED durationMs={} error={}",
                    job.getId(),
                    Duration.between(startedAt, Instant.now()).toMillis(),
                    e.getMessage());
            throw new IllegalStateException(e);
        } finally {
            jobRepository.saveAndFlush(job);
        }
    }
}
