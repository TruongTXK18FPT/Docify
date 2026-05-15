package com.docify.docify_be.conversion.service;

import com.docify.docify_be.conversion.dto.FileDownloadDto;
import com.docify.docify_be.conversion.dto.JobResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ConversionService {
    JobResponse createConversionJob(MultipartFile file, String targetType);
    JobResponse getJobStatus(UUID jobId);
    FileDownloadDto getDownloadStream(UUID jobId);
    List<JobResponse> getUserHistory();
    JobResponse retryJob(UUID jobId);
    JobResponse cancelJob(UUID jobId);
}
