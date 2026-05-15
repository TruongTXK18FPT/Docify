package com.docify.docify_be.file.service;

import com.docify.docify_be.common.exception.DocifyException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${docify.storage.local.temp-dir:/tmp/docify}")
    private String tempDirPath;

    @Value("${docify.storage.local.result-dir:/tmp/docify/results}")
    private String resultDirPath;

    private Path rootLocation;
    private Path tempLocation;
    private Path resultLocation;

    @PostConstruct
    public void init() {
        this.tempLocation = Paths.get(tempDirPath).toAbsolutePath().normalize();
        this.resultLocation = Paths.get(resultDirPath).toAbsolutePath().normalize();
        this.rootLocation = commonRoot(tempLocation, resultLocation);
        try {
            Files.createDirectories(tempLocation);
            Files.createDirectories(resultLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DocifyException("EMPTY_FILE", "Failed to store empty file.");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String generatedFileName = UUID.randomUUID() + extension;

        try {
            Path destinationFile = tempLocation.resolve(generatedFileName).normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return toStoragePath(destinationFile);
        } catch (IOException e) {
            throw new DocifyException("STORAGE_ERROR", "Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public String save(InputStream inputStream, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String generatedFileName = UUID.randomUUID() + extension;

        try {
            Path destinationFile = tempLocation.resolve(generatedFileName).normalize().toAbsolutePath();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return toStoragePath(destinationFile);
        } catch (IOException e) {
            throw new DocifyException("STORAGE_ERROR", "Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public String saveResult(InputStream inputStream, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String generatedFileName = UUID.randomUUID() + extension;

        try {
            Path destinationFile = resultLocation.resolve(generatedFileName).normalize().toAbsolutePath();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return toStoragePath(destinationFile);
        } catch (IOException e) {
            throw new DocifyException("STORAGE_ERROR", "Failed to store result file: " + e.getMessage());
        }
    }

    @Override
    public InputStream read(String path) {
        try {
            Path file = resolveStoragePath(path);
            if (!file.startsWith(rootLocation)) {
                throw new DocifyException("PATH_TRAVERSAL", "Cannot read file outside current directory.");
            }
            return new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
            throw new DocifyException("FILE_NOT_FOUND", "Could not read file: " + path);
        }
    }

    @Override
    public void delete(String path) {
        if (path == null || path.trim().isEmpty()) {
            return;
        }
        try {
            Path file = resolveStoragePath(path);
            if (!file.startsWith(rootLocation)) {
                return; // Protection
            }
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Log warning in real implementation
            System.err.println("Failed to delete file " + path + ": " + e.getMessage());
        }
    }

    private Path resolveStoragePath(String path) {
        return rootLocation.resolve(path).normalize().toAbsolutePath();
    }

    private String toStoragePath(Path path) {
        return rootLocation.relativize(path.toAbsolutePath().normalize()).toString().replace(File.separatorChar, '/');
    }

    private Path commonRoot(Path first, Path second) {
        Path root = first;
        while (root != null && !second.startsWith(root)) {
            root = root.getParent();
        }
        return root != null ? root : first.getParent();
    }
}
