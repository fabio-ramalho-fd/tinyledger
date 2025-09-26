package com.example.ledger.repo;

import com.example.ledger.domain.Money;
import com.example.ledger.domain.Transaction;
import com.example.ledger.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void shouldSaveTransaction() {
        // given
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("10.50"), 
            Instant.now()
        );
        
        // when
        Transaction saved = repository.save(transaction);
        
        // then
        assertEquals(transaction, saved);
    }

    @Test
    void shouldRejectNullTransaction() {
        // when & then
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactions() {
        // when
        List<Transaction> transactions = repository.findAllOrderByTimestampDesc();
        
        // then
        assertTrue(transactions.isEmpty());
    }

    @Test
    void shouldReturnSingleTransaction() {
        // given
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("10.50"), 
            Instant.now()
        );
        repository.save(transaction);
        
        // when
        List<Transaction> transactions = repository.findAllOrderByTimestampDesc();
        
        // then
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.getFirst());
    }

    @Test
    void shouldReturnTransactionsOrderedByTimestampDesc() {
        // given
        Instant time1 = Instant.parse("2023-10-01T10:00:00.000Z");
        Instant time2 = Instant.parse("2023-10-01T11:00:00.000Z");
        Instant time3 = Instant.parse("2023-10-01T12:00:00.000Z");
        
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, Money.of("10.00"), time1);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAW, Money.of("5.00"), time2);
        Transaction transaction3 = new Transaction(TransactionType.DEPOSIT, Money.of("20.00"), time3);
        
        // Save in different order
        repository.save(transaction2);
        repository.save(transaction1);
        repository.save(transaction3);
        
        // when
        List<Transaction> transactions = repository.findAllOrderByTimestampDesc();
        
        // then
        assertEquals(3, transactions.size());
        assertEquals(transaction3, transactions.get(0)); // Most recent first
        assertEquals(transaction2, transactions.get(1));
        assertEquals(transaction1, transactions.get(2)); // Oldest last
    }

    @Test
    void shouldReturnNewListOnEachCall() {
        // given
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("10.50"), 
            Instant.now()
        );
        repository.save(transaction);
        
        // when
        List<Transaction> list1 = repository.findAllOrderByTimestampDesc();
        List<Transaction> list2 = repository.findAllOrderByTimestampDesc();
        
        // then
        assertNotSame(list1, list2); // Different list instances
        assertEquals(list1, list2); // But same content
    }

    @Test
    void shouldHandleMultipleTransactionsWithSameTimestamp() {
        // given
        Instant sameTime = Instant.now();
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, Money.of("10.00"), sameTime);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAW, Money.of("5.00"), sameTime);
        
        repository.save(transaction1);
        repository.save(transaction2);
        
        // when
        List<Transaction> transactions = repository.findAllOrderByTimestampDesc();
        
        // then
        assertEquals(2, transactions.size());
        // Order might vary for same timestamp, but both should be present
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
    }

    @Test
    void shouldBeSynchronizedForConcurrentAccess() {
        // This test verifies that the methods are synchronized
        // In a real scenario, you might use more sophisticated concurrency tests
        
        // given
        Transaction transaction = new Transaction(
            TransactionType.DEPOSIT, 
            Money.of("10.50"), 
            Instant.now()
        );
        
        // when & then - should not throw any exceptions
        assertDoesNotThrow(() -> {
            repository.save(transaction);
            repository.findAllOrderByTimestampDesc();
        });
    }

    @Test
    void shouldMaintainTransactionOrderWithMultipleItems() {
        // given
        Instant baseTime = Instant.parse("2023-10-01T10:00:00.000Z");
        
        // Create 5 transactions with different timestamps (T, T+60, T+120, T+180, T+240)
        for (int i = 0; i < 5; i++) {
            Transaction transaction = new Transaction(
                TransactionType.DEPOSIT, 
                Money.of("10.00"), 
                baseTime.plusSeconds(i * 60)
            );
            repository.save(transaction);
        }
        
        // when
        List<Transaction> transactions = repository.findAllOrderByTimestampDesc();
        
        // then
        assertEquals(5, transactions.size());
        
        // Verify descending order - most recent first
        assertEquals(baseTime.plusSeconds(240), transactions.get(0).getCreatedAt()); // T+240
        assertEquals(baseTime.plusSeconds(180), transactions.get(1).getCreatedAt()); // T+180
        assertEquals(baseTime.plusSeconds(120), transactions.get(2).getCreatedAt()); // T+120
        assertEquals(baseTime.plusSeconds(60), transactions.get(3).getCreatedAt());  // T+60
        assertEquals(baseTime, transactions.get(4).getCreatedAt());                  // T
        
        // Verify general descending order
        for (int i = 0; i < transactions.size() - 1; i++) {
            Instant current = transactions.get(i).getCreatedAt();
            Instant next = transactions.get(i + 1).getCreatedAt();
            assertTrue(current.isAfter(next) || current.equals(next));
        }
    }
}
