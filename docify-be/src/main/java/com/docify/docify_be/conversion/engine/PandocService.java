package com.docify.docify_be.conversion.engine;

import com.docify.docify_be.common.exception.DocifyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PandocService implements ConversionEngine {

    @Value("${docify.storage.local.temp-dir:/tmp/docify}")
    private String tempDirPath;

    @Override
    public boolean supports(String sourceType, String targetType) {
        String src = sourceType != null ? sourceType.toLowerCase() : "";
        String tgt = targetType != null ? targetType.toLowerCase() : "";
        
        if (src.equals("md") || src.equals("markdown")) {
            return tgt.equals("docx") || tgt.equals("pdf");
        }
        if (src.equals("docx")) {
            return tgt.equals("md") || tgt.equals("markdown");
        }
        return false;
    }

    @Override
    public InputStream convert(InputStream sourceStream, String originalFilename) throws Exception {
        String baseName = UUID.randomUUID().toString();
        Path inputPath = Path.of(tempDirPath, baseName + "_input");
        Path outputPath = Path.of(tempDirPath, baseName + "_output");

        try {
            // Save input stream to temp file for Pandoc processing
            Files.copy(sourceStream, inputPath, StandardCopyOption.REPLACE_EXISTING);

            String srcExt = getExtension(originalFilename);
            String tgtExt = getTargetExtension(originalFilename); // Hack for MVP: infer from logic

            // Start Process
            ProcessBuilder pb = new ProcessBuilder("pandoc", inputPath.toString(), "-o", outputPath.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new DocifyException("PANDOC_TIMEOUT", "Pandoc process timed out.");
            }

            if (process.exitValue() != 0) {
                byte[] errorBytes = process.getInputStream().readAllBytes();
                String errorMsg = new String(errorBytes);
                throw new DocifyException("PANDOC_ERROR", "Pandoc conversion failed: " + errorMsg);
            }

            // Return input stream of output file
            return new FileInputStream(outputPath.toFile()) {
                @Override
                public void close() throws java.io.IOException {
                    super.close();
                    // Cleanup output temp file after stream is closed
                    Files.deleteIfExists(outputPath);
                }
            };
        } finally {
            // Cleanup input temp file immediately
            Files.deleteIfExists(inputPath);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private String getTargetExtension(String filename) {
        return "pdf"; // Default fallback
    }
}
