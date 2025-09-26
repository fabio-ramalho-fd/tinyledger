package com.example.ledger.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateTransactionWithValidParameters() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // then
        assertNotNull(transaction.getId());
        assertEquals(type, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(timestamp, transaction.getCreatedAt());
    }

    @Test
    void shouldGenerateUniqueIds() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when
        Transaction transaction1 = new Transaction(type, amount, timestamp);
        Transaction transaction2 = new Transaction(type, amount, timestamp);
        
        // then
        assertNotEquals(transaction1.getId(), transaction2.getId());
    }

    @Test
    void shouldRejectNullType() {
        // given
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when & then
        assertThrows(NullPointerException.class, 
            () -> new Transaction(null, amount, timestamp));
    }

    @Test
    void shouldRejectNullAmount() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when & then
        assertThrows(NullPointerException.class, 
            () -> new Transaction(type, null, timestamp));
    }

    @Test
    void shouldRejectNullTimestamp() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        
        // when & then
        assertThrows(NullPointerException.class, 
            () -> new Transaction(type, amount, null));
    }

    @Test
    void shouldCreateDepositTransaction() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("100.00");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // then
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
    }

    @Test
    void shouldCreateWithdrawTransaction() {
        // given
        TransactionType type = TransactionType.WITHDRAW;
        Money amount = Money.of("50.00");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        
        // when
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // then
        assertEquals(TransactionType.WITHDRAW, transaction.getType());
    }

    @Test
    void shouldNotBeEqualToNull() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // when & then
        assertNotEquals(null, transaction);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(type, amount, timestamp);
        String other = "transaction";
        
        // when & then
        assertNotEquals(other, transaction);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // when
        int hashCode1 = transaction.hashCode();
        int hashCode2 = transaction.hashCode();
        
        // then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        // given
        TransactionType type = TransactionType.DEPOSIT;
        Money amount = Money.of("10.50");
        Instant timestamp = Instant.parse("2023-10-01T10:15:30.123Z");
        Transaction transaction = new Transaction(type, amount, timestamp);
        
        // when
        String result = transaction.toString();
        
        // then
        assertTrue(result.contains("Transaction{"));
        assertTrue(result.contains("id=" + transaction.getId()));
        assertTrue(result.contains("type=DEPOSIT"));
        assertTrue(result.contains("amount=10.50"));
        assertTrue(result.contains("createdAt=2023-10-01T10:15:30.123Z"));
    }
}
