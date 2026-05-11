package com.docify.docify_be.conversion.service.impl;

import com.docify.docify_be.common.config.RabbitMQConfig;
import com.docify.docify_be.common.exception.DocifyException;
import com.docify.docify_be.conversion.dto.FileDownloadDto;
import com.docify.docify_be.conversion.dto.JobResponse;
import com.docify.docify_be.conversion.service.ConversionService;
import com.docify.docify_be.file.service.StorageService;
import com.docify.docify_be.job.entity.ConversionJob;
import com.docify.docify_be.job.enums.JobStatus;
import com.docify.docify_be.job.repository.ConversionJobRepository;
import com.docify.docify_be.queue.message.ConversionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.context.SecurityContextHolder;
import com.docify.docify_be.common.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

    private final StorageService storageService;
    private final ConversionJobRepository jobRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public JobResponse createConversionJob(MultipartFile file, String targetType) {
        String originalFilename = file.getOriginalFilename();
        String sourceType = getExtension(originalFilename);

        // Lưu file thô người dùng tải lên thông qua StorageService
        String sourceFileUrl = storageService.save(file);

        // Get current authenticated user
        UUID currentUserId = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            currentUserId = ((UserPrincipal) principal).getId();
        }

        // Khởi tạo Entity ConversionJob
        ConversionJob job = ConversionJob.builder()
                .status(JobStatus.PENDING)
                .sourceType(sourceType)
                .targetType(targetType)
                .originalFileName(originalFilename)
                .sourceFileUrl(sourceFileUrl)
                .progress(0)
                .userId(currentUserId)
                .build();

        // Lưu metadata vào CSDL
        jobRepository.save(job);

        // Đẩy Message vào RabbitMQ cho worker xử lý async (không làm HTTP request bị block)
        ConversionMessage message = ConversionMessage.builder()
                .jobId(job.getId())
                .sourceType(sourceType)
                .targetType(targetType)
                .sourceFileUrl(sourceFileUrl)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONVERSION_EXCHANGE,
                RabbitMQConfig.CONVERSION_ROUTING_KEY,
                message
        );

        return mapToResponse(job);
    }

    @Override
    public JobResponse getJobStatus(UUID jobId) {
        ConversionJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new DocifyException("JOB_NOT_FOUND", "Cannot find job with ID: " + jobId));
        return mapToResponse(job);
    }

    @Override
    public FileDownloadDto getDownloadStream(UUID jobId) {
        ConversionJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new DocifyException("JOB_NOT_FOUND", "Cannot find job with ID: " + jobId));

        if (job.getStatus() != JobStatus.COMPLETED || job.getResultFileUrl() == null) {
            throw new DocifyException("FILE_NOT_READY", "Result file is not ready for download.");
        }

        InputStream stream = storageService.read(job.getResultFileUrl());
        
        // Tạo header filename cho kết quả VD: report.docx -> report.pdf
        String resultFilename = job.getOriginalFileName();
        if (resultFilename != null && resultFilename.contains(".")) {
            resultFilename = resultFilename.substring(0, resultFilename.lastIndexOf(".")) + "." + job.getTargetType();
        } else {
            resultFilename = "converted_file." + job.getTargetType();
        }

        return FileDownloadDto.builder()
                .resource(new InputStreamResource(stream))
                .filename(resultFilename)
                .contentType("application/octet-stream")
                .build();
    }

    @Override
    public List<JobResponse> getUserHistory() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new DocifyException("UNAUTHORIZED", "User must be logged in to view history");
        }
        UUID currentUserId = ((UserPrincipal) principal).getId();
        return jobRepository.findByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private JobResponse mapToResponse(ConversionJob job) {
        return JobResponse.builder()
                .id(job.getId())
                .status(job.getStatus().name())
                .sourceType(job.getSourceType())
                .targetType(job.getTargetType())
                .progress(job.getProgress())
                .errorCode(job.getErrorCode())
                .build();
    }
}
