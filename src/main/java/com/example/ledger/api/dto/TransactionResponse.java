package com.example.ledger.api.dto;

import com.example.ledger.domain.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Transaction details")
public record TransactionResponse(
        
        @Schema(description = "Unique transaction identifier")
        UUID id,
        
        @Schema(description = "Type of transaction", example = "DEPOSIT")
        String type,
        
        @Schema(description = "Amount in EUR", example = "100.50")
        BigDecimal amount,
        
        @Schema(description = "Timestamp when the transaction was created", example = "2023-10-01T10:15:30.123Z")
        Instant createdAt
) {
    
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAmount().getAmount(),
                transaction.getCreatedAt()
        );
    }
}
