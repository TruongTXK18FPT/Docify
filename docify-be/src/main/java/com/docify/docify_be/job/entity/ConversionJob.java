package com.docify.docify_be.job.entity;

import com.docify.docify_be.job.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversion_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionJob {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private JobStatus status;

    @Column(name = "source_type", length = 20)
    private String sourceType;

    @Column(name = "target_type", length = 20)
    private String targetType;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "source_file_url", columnDefinition = "TEXT")
    private String sourceFileUrl;

    @Column(name = "result_file_url", columnDefinition = "TEXT")
    private String resultFileUrl;

    @Column(name = "progress")
    private Integer progress = 0;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "user_id")
    private UUID userId;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
