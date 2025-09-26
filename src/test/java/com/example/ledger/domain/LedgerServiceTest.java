package com.example.ledger.domain;

import com.example.ledger.repo.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private InMemoryTransactionRepository transactionRepository;

    @Mock
    private Clock clock;

    private LedgerService ledgerService;

    private final Instant fixedInstant = Instant.parse("2023-10-01T10:15:30.123Z");

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService(transactionRepository, clock);
    }

    @Test
    void shouldRejectNullTransactionRepository() {
        // when & then
        assertThrows(NullPointerException.class, 
            () -> new LedgerService(null, clock));
    }

    @Test
    void shouldRejectNullClock() {
        // when & then
        assertThrows(NullPointerException.class, 
            () -> new LedgerService(transactionRepository, null));
    }

    @Test
    void shouldDepositMoney() {
        // given
        Money amount = Money.of("100.50");
        Transaction expectedTransaction = new Transaction(TransactionType.DEPOSIT, amount, fixedInstant);
        when(clock.instant()).thenReturn(fixedInstant);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);
        
        // when
        Transaction result = ledgerService.deposit(amount);
        
        // then
        assertEquals(expectedTransaction, result);
        verify(transactionRepository).save(any(Transaction.class));
        verify(clock).instant();
    }

    @Test
    void shouldRejectNullAmountForDeposit() {
        // when & then
        assertThrows(NullPointerException.class, () -> ledgerService.deposit(null));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldWithdrawMoneyWhenSufficientFunds() {
        // given
        Money withdrawAmount = Money.of("50.00");
        Money currentBalance = Money.of("100.00");
        
        // Mock existing deposit transaction
        Transaction existingDeposit = new Transaction(TransactionType.DEPOSIT, currentBalance, fixedInstant);
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(List.of(existingDeposit));
        
        Transaction expectedWithdraw = new Transaction(TransactionType.WITHDRAW, withdrawAmount, fixedInstant);
        when(clock.instant()).thenReturn(fixedInstant);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedWithdraw);
        
        // when
        Transaction result = ledgerService.withdraw(withdrawAmount);
        
        // then
        assertEquals(expectedWithdraw, result);
        verify(transactionRepository).findAllOrderByTimestampDesc();
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldRejectWithdrawWhenInsufficientFunds() {
        // given
        Money withdrawAmount = Money.of("150.00");
        Money currentBalance = Money.of("100.00");
        
        Transaction existingDeposit = new Transaction(TransactionType.DEPOSIT, currentBalance, fixedInstant);
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(List.of(existingDeposit));
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> ledgerService.withdraw(withdrawAmount));
        
        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertTrue(exception.getMessage().contains("100.00"));
        assertTrue(exception.getMessage().contains("150.00"));
        
        verify(transactionRepository).findAllOrderByTimestampDesc();
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldRejectWithdrawFromEmptyAccount() {
        // given
        Money withdrawAmount = Money.of("10.00");
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(Collections.emptyList());
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> ledgerService.withdraw(withdrawAmount));
        
        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertTrue(exception.getMessage().contains("0.00"));
        assertTrue(exception.getMessage().contains("10.00"));
    }

    @Test
    void shouldRejectNullAmountForWithdraw() {
        // when & then
        assertThrows(NullPointerException.class, () -> ledgerService.withdraw(null));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldCalculateBalanceWithNoTransactions() {
        // given
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(Collections.emptyList());
        
        // when
        Money balance = ledgerService.getBalance();
        
        // then
        assertEquals(Money.of("0.00"), balance);
        verify(transactionRepository).findAllOrderByTimestampDesc();
    }

    @Test
    void shouldCalculateBalanceWithOnlyDeposits() {
        // given
        Transaction deposit1 = new Transaction(TransactionType.DEPOSIT, Money.of("100.00"), fixedInstant);
        Transaction deposit2 = new Transaction(TransactionType.DEPOSIT, Money.of("50.25"), fixedInstant);
        
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(Arrays.asList(deposit2, deposit1)); // Order doesn't matter for balance calculation
        
        // when
        Money balance = ledgerService.getBalance();
        
        // then
        assertEquals(Money.of("150.25"), balance);
    }

    @Test
    void shouldCalculateBalanceWithDepositsAndWithdrawals() {
        // given
        Transaction deposit1 = new Transaction(TransactionType.DEPOSIT, Money.of("100.00"), fixedInstant);
        Transaction withdraw1 = new Transaction(TransactionType.WITHDRAW, Money.of("30.50"), fixedInstant);
        Transaction deposit2 = new Transaction(TransactionType.DEPOSIT, Money.of("25.75"), fixedInstant);
        
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(Arrays.asList(deposit2, withdraw1, deposit1));
        
        // when
        Money balance = ledgerService.getBalance();
        
        // then
        assertEquals(Money.of("95.25"), balance); // 100.00 - 30.50 + 25.75
    }

    @Test
    void shouldGetAllTransactions() {
        // given
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, Money.of("100.00"), fixedInstant);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAW, Money.of("30.50"), fixedInstant);
        List<Transaction> expectedTransactions = Arrays.asList(transaction2, transaction1);
        
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(expectedTransactions);
        
        // when
        List<Transaction> result = ledgerService.getAllTransactions();
        
        // then
        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findAllOrderByTimestampDesc();
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactions() {
        // given
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(Collections.emptyList());
        
        // when
        List<Transaction> result = ledgerService.getAllTransactions();
        
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleConcurrentDeposits() throws InterruptedException {
        // given
        Money amount = Money.of("10.00");
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, fixedInstant);
        when(clock.instant()).thenReturn(fixedInstant);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // when - execute 10 concurrent deposits
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                assertDoesNotThrow(() -> ledgerService.deposit(amount));
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        
        // then - verify all deposits were processed
        verify(transactionRepository, times(10)).save(any(Transaction.class));
    }

    @Test
    void shouldHandleConcurrentWithdrawals() throws InterruptedException {
        // given
        Money withdrawAmount = Money.of("10.00");
        Money largeBalance = Money.of("1000.00"); // Large enough balance for all withdrawals
        
        Transaction existingDeposit = new Transaction(TransactionType.DEPOSIT, largeBalance, fixedInstant);
        when(transactionRepository.findAllOrderByTimestampDesc())
            .thenReturn(List.of(existingDeposit));
        
        Transaction withdrawal = new Transaction(TransactionType.WITHDRAW, withdrawAmount, fixedInstant);
        when(clock.instant()).thenReturn(fixedInstant);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawal);
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // when - execute 5 concurrent withdrawals
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                assertDoesNotThrow(() -> ledgerService.withdraw(withdrawAmount));
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        
        // then - verify all withdrawals were processed (10 saves: 1 from setup + 5 from concurrent operations)
        verify(transactionRepository, atLeast(5)).save(any(Transaction.class));
    }

    @Test
    void shouldUseClock() {
        // given
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        LedgerService service = new LedgerService(transactionRepository, fixedClock);
        
        Money amount = Money.of("100.00");
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, fixedInstant);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        // when
        service.deposit(amount);
        // then
        verify(transactionRepository).save(argThat(t -> 
            t.getCreatedAt().equals(fixedInstant)
        ));
    }
}
