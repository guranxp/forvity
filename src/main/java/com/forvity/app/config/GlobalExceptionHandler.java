package com.forvity.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> handleNotFound(final NoSuchElementException ex) {
        log.warn("Resource not found", kv("message", ex.getMessage()));
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(final IllegalStateException ex) {
        log.warn("Business rule violation", kv("message", ex.getMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(final MethodArgumentNotValidException ex) {
        final var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        org.springframework.validation.FieldError::getField,
                        e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid"
                ));
        log.warn("Validation failed", kv("errors", errors));
        return ResponseEntity.badRequest().body(errors);
    }
}