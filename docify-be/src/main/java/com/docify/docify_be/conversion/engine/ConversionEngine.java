package com.docify.docify_be.conversion.engine;

import java.io.InputStream;

public interface ConversionEngine {
    boolean supports(String sourceType, String targetType);
    InputStream convert(InputStream sourceStream, String originalFilename) throws Exception;
}