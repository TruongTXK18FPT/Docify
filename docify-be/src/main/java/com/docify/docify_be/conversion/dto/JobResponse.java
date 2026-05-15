package com.docify.docify_be.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private UUID id;
    private String fileName;
    private Long fileSize;
    private String status;
    private String sourceType;
    private String targetType;
    private Integer progress;
    private String errorCode;
    private String errorMessage;
    private Instant createdAt;
    private Instant completedAt;
    private Instant expiresAt;
    private String downloadUrl;
}
