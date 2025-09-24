package com.example.ledger.api.dto;

import com.example.ledger.domain.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Current account balance")
public record BalanceResponse(
        
        @Schema(description = "Current balance in EUR")
        BigDecimal balance
) {
    
    public static BalanceResponse from(Money money) {
        return new BalanceResponse(money.getAmount());
    }
}
