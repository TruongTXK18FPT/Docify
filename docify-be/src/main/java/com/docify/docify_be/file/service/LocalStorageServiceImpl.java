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

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(tempDirPath);
        try {
            Files.createDirectories(rootLocation);
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
        
        String generatedFileName = UUID.randomUUID().toString() + extension;
        
        try {
            Path destinationFile = rootLocation.resolve(Paths.get(generatedFileName)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new DocifyException("PATH_TRAVERSAL", "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return generatedFileName;
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
        
        String generatedFileName = UUID.randomUUID().toString() + extension;
        
        try {
            Path destinationFile = rootLocation.resolve(Paths.get(generatedFileName)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new DocifyException("PATH_TRAVERSAL", "Cannot store file outside current directory.");
            }
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return generatedFileName;
        } catch (IOException e) {
            throw new DocifyException("STORAGE_ERROR", "Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public InputStream read(String path) {
        try {
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            if (!file.getParent().equals(rootLocation.toAbsolutePath())) {
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
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            if (!file.getParent().equals(rootLocation.toAbsolutePath())) {
                return; // Protection
            }
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Log warning in real implementation
            System.err.println("Failed to delete file " + path + ": " + e.getMessage());
        }
    }
}
