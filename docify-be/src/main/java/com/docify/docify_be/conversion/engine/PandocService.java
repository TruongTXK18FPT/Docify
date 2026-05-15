package com.docify.docify_be.conversion.engine;

import com.docify.docify_be.common.exception.DocifyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${docify.pandoc.binary-path:pandoc}")
    private String pandocBinaryPath;

    @Value("${docify.pandoc.timeout-seconds:120}")
    private long timeoutSeconds;

    @Override
    public boolean supports(String sourceType, String targetType) {
        String src = normalize(sourceType);
        String tgt = normalize(targetType);

        if (src.equals("md") || src.equals("markdown")) {
            return tgt.equals("docx") || tgt.equals("pdf");
        }
        if (src.equals("docx")) {
            return tgt.equals("md") || tgt.equals("markdown");
        }
        return false;
    }

    @Override
    public InputStream convert(InputStream sourceStream, String sourceType, String targetType, String originalFilename) throws Exception {
        Files.createDirectories(Path.of(tempDirPath));

        String baseName = UUID.randomUUID().toString();
        String sourceExt = normalize(sourceType).equals("markdown") ? "md" : normalize(sourceType);
        String targetExt = normalize(targetType).equals("markdown") ? "md" : normalize(targetType);
        Path inputPath = Path.of(tempDirPath, baseName + "_input." + sourceExt).toAbsolutePath().normalize();
        Path outputPath = Path.of(tempDirPath, baseName + "_output." + targetExt).toAbsolutePath().normalize();

        try {
            Files.copy(sourceStream, inputPath, StandardCopyOption.REPLACE_EXISTING);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    pandocBinaryPath,
                    inputPath.toString(),
                    "-o",
                    outputPath.toString()
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new DocifyException("PANDOC_TIMEOUT", "Pandoc process timed out.");
            }

            if (process.exitValue() != 0) {
                String errorMsg = new String(process.getInputStream().readAllBytes());
                throw new DocifyException("PANDOC_ERROR", "Pandoc conversion failed: " + errorMsg);
            }

            return new FileInputStream(outputPath.toFile()) {
                @Override
                public void close() throws java.io.IOException {
                    super.close();
                    Files.deleteIfExists(outputPath);
                }
            };
        } finally {
            Files.deleteIfExists(inputPath);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
