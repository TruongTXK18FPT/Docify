package com.docify.docify_be.common.exception;

public class DocifyException extends RuntimeException {
    private final String code;

    public DocifyException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
