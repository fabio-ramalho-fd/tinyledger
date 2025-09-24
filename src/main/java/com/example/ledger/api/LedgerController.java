package com.example.ledger.api;

import com.example.ledger.api.dto.BalanceResponse;
import com.example.ledger.api.dto.TransactionRequest;
import com.example.ledger.api.dto.TransactionResponse;
import com.example.ledger.domain.LedgerService;
import com.example.ledger.domain.Money;
import com.example.ledger.domain.Transaction;
import com.example.ledger.domain.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/ledger")
@Tag(name = "Ledger", description = "Ledger API for managing financial transactions")
public class LedgerController {
    
    private final LedgerService ledgerService;
    
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }
    
    @Operation(summary = "Record a new transaction", description = "Creates a new deposit or withdrawal transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds")
    })
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> recordTransaction(@Valid @RequestBody TransactionRequest request) {
        Money amount = Money.of(request.amount());
        TransactionType type = TransactionType.valueOf(request.type().toUpperCase());
        
        Transaction transaction = switch (type) {
            case DEPOSIT -> ledgerService.deposit(amount);
            case WITHDRAW -> ledgerService.withdraw(amount);
        };
        
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
    
    @Operation(summary = "Get current balance", description = "Returns the current account balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully")
    })
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        Money balance = ledgerService.getBalance();
        return ResponseEntity.ok(BalanceResponse.from(balance));
    }
    
    @Operation(summary = "Get transaction history", description = "Returns all transactions ordered by timestamp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully")
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions() {
        List<Transaction> transaction = ledgerService.getAllTransactions();
        List<TransactionResponse> response = transaction.stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
