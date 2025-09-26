package com.example.ledger.api.dto;

import com.example.ledger.domain.Money;
import com.example.ledger.domain.Transaction;
import com.example.ledger.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResponseTest {

    @Test
    void shouldCreateFromDepositTransaction() {
        // given
        Money amount = Money.of("100.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, timestamp);
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertEquals(transaction.getId(), response.id());
        assertEquals("DEPOSIT", response.type());
        assertEquals(amount.getAmount(), response.amount());
        assertEquals(timestamp, response.createdAt());
    }

    @Test
    void shouldCreateFromWithdrawTransaction() {
        // given
        Money amount = Money.of("50.25");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount, timestamp);
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertEquals(transaction.getId(), response.id());
        assertEquals("WITHDRAW", response.type());
        assertEquals(amount.getAmount(), response.amount());
        assertEquals(timestamp, response.createdAt());
    }

    @Test
    void shouldCreateResponseWithAllFields() {
        // given
        Money amount = Money.of("75.00");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, timestamp);
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertNotNull(response.id());
        assertNotNull(response.type());
        assertNotNull(response.amount());
        assertNotNull(response.createdAt());
        
        assertEquals(transaction.getId(), response.id());
        assertEquals(transaction.getType().name(), response.type());
        assertEquals(transaction.getAmount().getAmount(), response.amount());
        assertEquals(transaction.getCreatedAt(), response.createdAt());
    }

    @Test
    void shouldPreserveTransactionId() {
        // given
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("100.00"), 
            Instant.parse("2023-10-01T10:15:30.123Z")
        );
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertEquals(transaction.getId(), response.id());
    }

    @Test
    void shouldConvertTransactionTypeToString() {
        // given
        Transaction depositTransaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("100.00"), 
            Instant.parse("2023-10-01T10:15:30.123Z")
        );
        Transaction withdrawTransaction = new Transaction(
            TransactionType.WITHDRAW, 
            Money.of("50.00"), 
            Instant.parse("2023-10-01T10:15:30.123Z")
        );
        
        // when
        TransactionResponse depositResponse = TransactionResponse.from(depositTransaction);
        TransactionResponse withdrawResponse = TransactionResponse.from(withdrawTransaction);
        
        // then
        assertEquals("DEPOSIT", depositResponse.type());
        assertEquals("WITHDRAW", withdrawResponse.type());
    }

    @Test
    void shouldPreserveAmountPrecision() {
        // given
        Money amount = Money.of("123.45");
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            amount, 
            Instant.parse("2023-10-01T10:15:30.123Z")
        );
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertEquals(amount.getAmount(), response.amount());
        assertEquals("123.45", response.amount().toString());
    }

    @Test
    void shouldPreserveTimestamp() {
        // given
        Instant specificTime = Instant.parse("2023-12-25T15:30:45.678Z");
        Transaction transaction = new Transaction(
            TransactionType.WITHDRAW, 
            Money.of("25.50"), 
            specificTime
        );
        
        // when
        TransactionResponse response = TransactionResponse.from(transaction);
        
        // then
        assertEquals(specificTime, response.createdAt());
    }
}
