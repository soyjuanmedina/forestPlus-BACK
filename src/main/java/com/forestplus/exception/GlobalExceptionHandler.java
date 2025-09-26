package com.forestplus.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForestPlusException.class)
    public ResponseEntity<ErrorResponse> handleForestPlusException(ForestPlusException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), ex.getStatus());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    public static record ErrorResponse(String message, int status) {}
}
