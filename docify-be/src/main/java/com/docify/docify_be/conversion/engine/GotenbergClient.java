package com.docify.docify_be.conversion.engine;

import com.docify.docify_be.common.exception.DocifyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@Service
public class GotenbergClient implements ConversionEngine {

    @Value("${docify.engine.gotenberg.url:http://localhost:3000}")
    private String gotenbergUrl;

    private final RestTemplate restTemplate;

    public GotenbergClient() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean supports(String sourceType, String targetType) {
        if (targetType == null || !targetType.equalsIgnoreCase("pdf")) return false;
        String src = sourceType != null ? sourceType.toLowerCase() : "";
        return src.equals("docx") || src.equals("doc") || src.equals("pptx") || src.equals("ppt");
    }

    @Override
    public InputStream convert(InputStream sourceStream, String originalFilename) throws Exception {
        String endpoint = gotenbergUrl + "/forms/libreoffice/convert";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new InputStreamResource(sourceStream) {
            @Override
            public String getFilename() {
                return originalFilename;
            }
            @Override
            public long contentLength() {
                return -1; // Unknown length
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<org.springframework.core.io.Resource> response = restTemplate.postForEntity(
                    endpoint,
                    requestEntity,
                    org.springframework.core.io.Resource.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getInputStream();
            } else {
                throw new DocifyException("GOTENBERG_ERROR", "Failed to convert document: HTTP " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            throw new DocifyException("GOTENBERG_ERROR", "Error communicating with Gotenberg: " + e.getMessage());
        }
    }
}
