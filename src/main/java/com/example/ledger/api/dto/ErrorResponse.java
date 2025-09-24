package com.example.ledger.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Error response")
public record ErrorResponse(
        
        @Schema(description = "Error message")
        String message,
        
        @Schema(description = "Error code")
        String code,
        
        @Schema(description = "Timestamp when the error occurred")
        Instant timestamp
) {
    
    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(message, code, Instant.now());
    }
}
