package com.docify.docify_be.conversion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadDto {
    private InputStreamResource resource;
    private String filename;
    private String contentType;
}