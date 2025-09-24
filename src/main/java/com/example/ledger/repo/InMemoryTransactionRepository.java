package com.example.ledger.repo;

import com.example.ledger.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Repository
public class InMemoryTransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    public synchronized Transaction save(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction can't be null");
        transactions.add(transaction);
        return transaction;
    }

    public synchronized List<Transaction> findAllOrderByTimestampDesc() {
        List<Transaction> result = new ArrayList<>(transactions);
        result.sort(Comparator.comparing(Transaction::getCreatedAt).reversed());
        return result;
    }
}
