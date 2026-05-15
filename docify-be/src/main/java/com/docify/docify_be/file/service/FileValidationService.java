package com.docify.docify_be.file.service;

import com.docify.docify_be.common.exception.DocifyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class FileValidationService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("md", "markdown", "docx", "pptx");
    private static final Map<String, Set<String>> SUPPORTED_CONVERSIONS = Map.of(
            "md", Set.of("pdf", "docx"),
            "markdown", Set.of("pdf", "docx"),
            "docx", Set.of("pdf", "md", "markdown"),
            "pptx", Set.of("pdf")
    );

    @Value("${docify.conversion.max-file-size-mb:50}")
    private long maxFileSizeMb;

    public void validateForConversion(MultipartFile file, String sourceType, String targetType) {
        if (file == null || file.isEmpty()) {
            throw new DocifyException("EMPTY_FILE", "File is empty.");
        }

        long maxBytes = maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new DocifyException("FILE_TOO_LARGE", "File exceeds " + maxFileSizeMb + "MB.");
        }

        String src = normalize(sourceType);
        String target = normalize(targetType);
        if (!ALLOWED_EXTENSIONS.contains(src)) {
            throw new DocifyException("INVALID_FILE_TYPE", "Unsupported source file type: " + sourceType);
        }
        if (!SUPPORTED_CONVERSIONS.getOrDefault(src, Set.of()).contains(target)) {
            throw new DocifyException("UNSUPPORTED_CONVERSION", "Unsupported conversion: " + sourceType + " to " + targetType);
        }

        validateMagicNumber(file, src);
    }

    private void validateMagicNumber(MultipartFile file, String sourceType) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(8);
            if (sourceType.equals("docx") || sourceType.equals("pptx")) {
                if (header.length < 4 || header[0] != 'P' || header[1] != 'K' || header[2] != 3 || header[3] != 4) {
                    throw new DocifyException("INVALID_MIME_TYPE", "Office files must be valid DOCX/PPTX archives.");
                }
                return;
            }

            if (sourceType.equals("md") || sourceType.equals("markdown")) {
                String prefix = new String(header, StandardCharsets.UTF_8);
                if (prefix.indexOf('\0') >= 0) {
                    throw new DocifyException("INVALID_MIME_TYPE", "Markdown file must be text.");
                }
            }
        } catch (IOException e) {
            throw new DocifyException("INVALID_FILE_TYPE", "Could not inspect uploaded file.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }
}
