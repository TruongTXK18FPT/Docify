package com.docify.docify_be.conversion.service.impl;

import com.docify.docify_be.common.config.RabbitMQConfig;
import com.docify.docify_be.common.exception.DocifyException;
import com.docify.docify_be.common.security.UserPrincipal;
import com.docify.docify_be.conversion.dto.FileDownloadDto;
import com.docify.docify_be.conversion.dto.JobResponse;
import com.docify.docify_be.conversion.service.ConversionService;
import com.docify.docify_be.file.service.FileValidationService;
import com.docify.docify_be.file.service.StorageService;
import com.docify.docify_be.job.entity.ConversionJob;
import com.docify.docify_be.job.enums.JobStatus;
import com.docify.docify_be.job.repository.ConversionJobRepository;
import com.docify.docify_be.queue.message.ConversionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

    private static final long DEFAULT_TTL_HOURS = 1;

    private final StorageService storageService;
    private final FileValidationService fileValidationService;
    private final ConversionJobRepository jobRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public JobResponse createConversionJob(MultipartFile file, String targetType) {
        String originalFilename = file.getOriginalFilename();
        String sourceType = getExtension(originalFilename);
        String normalizedTargetType = normalizeType(targetType);

        fileValidationService.validateForConversion(file, sourceType, normalizedTargetType);
        String sourceFileUrl = storageService.save(file);

        ConversionJob job = ConversionJob.builder()
                .status(JobStatus.QUEUED)
                .sourceType(sourceType)
                .targetType(normalizedTargetType)
                .originalFileName(originalFilename)
                .fileSize(file.getSize())
                .sourceFileUrl(sourceFileUrl)
                .progress(0)
                .userId(getCurrentUserId())
                .expiresAt(Instant.now().plus(DEFAULT_TTL_HOURS, ChronoUnit.HOURS))
                .build();

        jobRepository.save(job);
        publishJob(job);
        return mapToResponse(job);
    }

    @Override
    public JobResponse getJobStatus(UUID jobId) {
        return mapToResponse(findOwnedJob(jobId));
    }

    @Override
    public FileDownloadDto getDownloadStream(UUID jobId) {
        ConversionJob job = findOwnedJob(jobId);

        if (job.getStatus() == JobStatus.EXPIRED) {
            throw new DocifyException("JOB_EXPIRED", "The converted file has expired.");
        }
        if (job.getStatus() != JobStatus.COMPLETED || job.getResultFileUrl() == null) {
            throw new DocifyException("FILE_NOT_READY", "Result file is not ready for download.");
        }

        InputStream stream = storageService.read(job.getResultFileUrl());
        String resultFilename = buildResultFilename(job);

        return FileDownloadDto.builder()
                .resource(new InputStreamResource(stream))
                .filename(resultFilename)
                .contentType("application/octet-stream")
                .build();
    }

    @Override
    public List<JobResponse> getUserHistory() {
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new DocifyException("UNAUTHORIZED", "User must be logged in to view history");
        }
        return jobRepository.findByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public JobResponse retryJob(UUID jobId) {
        ConversionJob job = findOwnedJob(jobId);
        if (job.getStatus() != JobStatus.FAILED) {
            throw new DocifyException("INVALID_JOB_STATE", "Only failed jobs can be retried.");
        }

        job.setStatus(JobStatus.QUEUED);
        job.setProgress(0);
        job.setErrorCode(null);
        job.setErrorMessage(null);
        job.setResultFileUrl(null);
        job.setCompletedAt(null);
        job.setExpiresAt(Instant.now().plus(DEFAULT_TTL_HOURS, ChronoUnit.HOURS));
        jobRepository.save(job);
        publishJob(job);
        return mapToResponse(job);
    }

    @Override
    @Transactional
    public JobResponse cancelJob(UUID jobId) {
        ConversionJob job = findOwnedJob(jobId);
        if (job.getStatus() == JobStatus.COMPLETED || job.getStatus() == JobStatus.FAILED || job.getStatus() == JobStatus.EXPIRED) {
            throw new DocifyException("INVALID_JOB_STATE", "This job can no longer be cancelled.");
        }

        job.setStatus(JobStatus.CANCELLED);
        job.setProgress(0);
        job.setErrorCode(null);
        job.setErrorMessage(null);
        storageService.delete(job.getSourceFileUrl());
        jobRepository.save(job);
        return mapToResponse(job);
    }

    private ConversionJob findOwnedJob(UUID jobId) {
        ConversionJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new DocifyException("JOB_NOT_FOUND", "Cannot find job with ID: " + jobId));
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null || job.getUserId() == null || !job.getUserId().equals(currentUserId)) {
            throw new DocifyException("JOB_NOT_FOUND", "Cannot find job with ID: " + jobId);
        }
        return job;
    }

    private UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        return null;
    }

    private void publishJob(ConversionJob job) {
        ConversionMessage message = ConversionMessage.builder()
                .jobId(job.getId())
                .sourceType(job.getSourceType())
                .targetType(job.getTargetType())
                .sourceFileUrl(job.getSourceFileUrl())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONVERSION_EXCHANGE,
                RabbitMQConfig.CONVERSION_ROUTING_KEY,
                message
        );
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return normalizeType(filename.substring(filename.lastIndexOf(".") + 1));
    }

    private String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase();
    }

    private String buildResultFilename(ConversionJob job) {
        String resultFilename = job.getOriginalFileName();
        if (resultFilename != null && resultFilename.contains(".")) {
            return resultFilename.substring(0, resultFilename.lastIndexOf(".")) + "." + job.getTargetType();
        }
        return "converted_file." + job.getTargetType();
    }

    private JobResponse mapToResponse(ConversionJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .fileName(job.getOriginalFileName())
                .fileSize(job.getFileSize())
                .status(job.getStatus().name())
                .sourceType(job.getSourceType())
                .targetType(job.getTargetType())
                .progress(job.getProgress())
                .errorCode(job.getErrorCode())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .completedAt(job.getCompletedAt())
                .expiresAt(job.getExpiresAt())
                .downloadUrl(job.getStatus() == JobStatus.COMPLETED ? "/api/conversions/" + job.getId() + "/download" : null)
                .build();
    }
}
