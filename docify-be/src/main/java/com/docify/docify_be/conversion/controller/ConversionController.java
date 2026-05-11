package com.docify.docify_be.conversion.controller;

import com.docify.docify_be.common.api.ApiResponse;
import com.docify.docify_be.conversion.dto.FileDownloadDto;
import com.docify.docify_be.conversion.dto.JobResponse;
import com.docify.docify_be.conversion.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversions")
@RequiredArgsConstructor
public class ConversionController {

    private final ConversionService conversionService;

    @GetMapping("/history")
    public ApiResponse<List<JobResponse>> getHistory() {
        return ApiResponse.success(conversionService.getUserHistory());
    }

    @PostMapping("/upload")
    public ApiResponse<JobResponse> uploadAndConvert(
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetType") String targetType) {
        
        JobResponse response = conversionService.createConversionJob(file, targetType);
        return ApiResponse.success(response);
    }

    @GetMapping("/{jobId}/status")
    public ApiResponse<JobResponse> getJobStatus(@PathVariable UUID jobId) {
        JobResponse response = conversionService.getJobStatus(jobId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{jobId}/download")
    public ResponseEntity<Resource> downloadResult(@PathVariable UUID jobId) {
        FileDownloadDto downloadDto = conversionService.getDownloadStream(jobId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadDto.getFilename() + "\"")
                .body(downloadDto.getResource());
    }
}
