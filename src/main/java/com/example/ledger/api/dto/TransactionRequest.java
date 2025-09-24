package com.example.ledger.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request to create a new transaction (deposit or withdrawal)")
public record TransactionRequest(
        
        @Schema(description = "Type of transaction", allowableValues = {"DEPOSIT", "WITHDRAW"})
        @NotNull(message = "Transaction type is required")
        String type,
        
        @Schema(description = "Amount in EUR")
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
        @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Amount can't have more than 2 decimals")
        BigDecimal amount
) {
}
