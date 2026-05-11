package com.docify.docify_be.file.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface StorageService {
    String save(MultipartFile file);
    String save(InputStream inputStream, String originalFilename);
    InputStream read(String path);
    void delete(String path);
}
