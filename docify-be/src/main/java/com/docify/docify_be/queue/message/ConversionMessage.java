package com.docify.docify_be.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionMessage {
    private UUID jobId;
    private String sourceType;
    private String targetType;
    private String sourceFileUrl;
}
