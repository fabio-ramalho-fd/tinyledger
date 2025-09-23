package com.example.ledger.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    private final BigDecimal amount;
    
    private Money(BigDecimal amount) {
        this.amount = amount.setScale(SCALE, ROUNDING_MODE);
    }
    
    public static Money of(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount can't be null");
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount can't be negative");
        }
        
        if (amount.scale() > SCALE) {
            throw new IllegalArgumentException("Amount can't have more than " + SCALE + " decimals");
        }
        
        return new Money(amount);
    }
    
    public static Money of(String amount) {
        Objects.requireNonNull(amount, "Amount string cannot be null");
        String trimmed = amount.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Amount string cannot be empty");
        }
        return of(new BigDecimal(trimmed));
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
    
    public Money add(Money other) {
        Objects.requireNonNull(other, "Money to add cannot be null");
        return new Money(this.amount.add(other.amount));
    }
    
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Money to subtract cannot be null");
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot subtract " + other + " from " + this + " as result would be negative");
        }
        return new Money(result);
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return Objects.equals(amount, money.amount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
    
    @Override
    public String toString() {
        return amount.toString();
    }
}
