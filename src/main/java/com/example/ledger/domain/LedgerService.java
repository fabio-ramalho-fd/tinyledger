package com.example.ledger.domain;

import com.example.ledger.repo.InMemoryTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.Objects;

@Service
public class LedgerService {

    private final InMemoryTransactionRepository transactionRepository;
    private final Clock clock;

    private final Object lock = new Object();

    public LedgerService(InMemoryTransactionRepository transactionRepository, Clock clock) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "TransactionRepository can´t be null");
        this.clock = Objects.requireNonNull(clock, "Clock can´t be null");
    }

    public Transaction deposit(Money amount) {
        Objects.requireNonNull(amount, "Amount can´t be null");
        synchronized (lock) {
            Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, clock.instant());
            return transactionRepository.save(transaction);
        }
    }

    public Transaction withdraw(Money amount) {
        Objects.requireNonNull(amount, "Amount can´t be null");
        synchronized (lock) {
            Money currentBalance = calculateBalanceInternal();
            if (currentBalance.getAmount().compareTo(amount.getAmount()) < 0) {
                throw new IllegalArgumentException(
                        "Insufficient funds: current balance is " + currentBalance + ", requested " + amount
                );
            }
            Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount, clock.instant());
            return transactionRepository.save(transaction);
        }
    }

    public Money getBalance() {
        synchronized (lock) {
            return calculateBalanceInternal();
        }
    }

    public List<Transaction> getAllTransactions() {
        synchronized (lock) {
            return transactionRepository.findAllOrderByTimestampDesc();
        }
    }

    private Money calculateBalanceInternal() {
        List<Transaction> allTransactions = transactionRepository.findAllOrderByTimestampDesc();
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : allTransactions) {
            switch (transaction.getType()) {
                case DEPOSIT -> balance = balance.add(transaction.getAmount().getAmount());
                case WITHDRAW -> balance = balance.subtract(transaction.getAmount().getAmount());
            }
        }

        return Money.of(balance);
    }
}
