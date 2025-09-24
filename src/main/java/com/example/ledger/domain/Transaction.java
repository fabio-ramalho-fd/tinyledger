package com.example.ledger.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Transaction {
    private final UUID id;
    private final TransactionType type;
    private final Money amount;
    private final Instant createdAt;

    public Transaction(TransactionType type, Money amount, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.type = Objects.requireNonNull(type, "Transaction type can't be null");
        this.amount = Objects.requireNonNull(amount, "Amount can't be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at timestamp can't be null");
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction transaction = (Transaction) obj;
        return Objects.equals(id, transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
