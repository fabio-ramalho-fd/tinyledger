package com.example.ledger.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Movement {
    private final UUID id;
    private final MovementType type;
    private final Money amount;
    private final Instant createdAt;
    
    private Movement(UUID id, MovementType type, Money amount, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.type = Objects.requireNonNull(type, "Movement type cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at timestamp cannot be null");
    }
    
    public static Movement create(MovementType type, Money amount, Instant createdAt) {
        return new Movement(UUID.randomUUID(), type, amount, createdAt);
    }
    
    public UUID getId() {
        return id;
    }
    
    public MovementType getType() {
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
        Movement movement = (Movement) obj;
        return Objects.equals(id, movement.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
