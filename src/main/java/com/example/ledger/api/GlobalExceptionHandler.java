package com.example.ledger.api;

import com.example.ledger.api.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.warn("Bad request: {}", ex.getMessage());

        String code = determineErrorCode(ex.getMessage());
        HttpStatus status = code.equals("INSUFFICIENT_FUNDS")
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.BAD_REQUEST;

        ErrorResponse error = ErrorResponse.of(ex.getMessage(), code);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        LOGGER.warn("Validation error: {}", message);
        ErrorResponse error = ErrorResponse.of(message, "VALIDATION_ERROR");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.warn("Bad request - malformed JSON or missing body: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of("Invalid request body", "BAD_REQUEST");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        LOGGER.warn("Bad request - unsupported media type: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of("Invalid request body", "BAD_REQUEST");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error", ex);
        ErrorResponse error = ErrorResponse.of("An unexpected error occurred", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String determineErrorCode(String message) {
        if (message == null) return "BAD_REQUEST";
        String lower = message.toLowerCase();
        if (lower.contains("insufficient funds")) return "INSUFFICIENT_FUNDS";
        if (lower.contains("negative")) return "NEGATIVE_AMOUNT";
        if (lower.contains("decimal places")) return "INVALID_PRECISION";
        if (lower.contains("transaction type")) return "INVALID_TRANSACTION_TYPE";
        return "BAD_REQUEST";
    }
}
