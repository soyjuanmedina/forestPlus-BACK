package com.forestplus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForestPlusException.class)
    public ResponseEntity<ErrorResponse> handleForestPlusException(ForestPlusException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), ex.getStatus());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // 👇 Nuevo método para manejar archivos demasiado grandes
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        ErrorResponse body = new ErrorResponse(
            "El archivo es demasiado grande. Tamaño máximo permitido: 20MB",
            HttpStatus.PAYLOAD_TOO_LARGE.value()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    public static record ErrorResponse(String message, int status) {}
}
