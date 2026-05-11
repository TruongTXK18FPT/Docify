package com.docify.docify_be.common.exception;

import com.docify.docify_be.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred: " + ex.getMessage());
    }
    
    @ExceptionHandler(DocifyException.class)
    public ApiResponse<Void> handleDocifyException(DocifyException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }
}
