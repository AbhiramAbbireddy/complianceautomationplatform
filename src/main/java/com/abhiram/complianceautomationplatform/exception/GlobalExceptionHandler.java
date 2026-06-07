package com.abhiram.complianceautomationplatform.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.abhiram.complianceautomationplatform.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now())
                                .status(404)
                                .message(
                                        ex.getMessage())
                                .build());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex) {
        return ResponseEntity.status(
                HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now())
                                .status(409)
                                .message(
                                        ex.getMessage())
                                .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now())
                                .status(400)
                                .message(
                                        ex.getMessage())
                                .build());
    }
}
