package com.example.ledger.domain;

import com.example.ledger.repo.InMemoryMovementRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.Objects;

@Service
public class LedgerService {

    private final InMemoryMovementRepository movementRepository;
    private final Clock clock;

    public LedgerService(InMemoryMovementRepository movementRepository, Clock clock) {
        this.movementRepository = Objects.requireNonNull(movementRepository, "MovementRepository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    public Movement deposit(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Movement movement = new Movement(MovementType.DEPOSIT, amount, clock.instant());
        return movementRepository.save(movement);
    }

    public Movement withdraw(Money amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");

        Money currentBalance = calculateBalance();
        if (currentBalance.getAmount().compareTo(amount.getAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds: current balance is " + currentBalance + ", requested " + amount
            );
        }

        Movement movement = new Movement(MovementType.WITHDRAW, amount, clock.instant());
        return movementRepository.save(movement);
    }

    public Money getBalance() {
        return calculateBalance();
    }

    public List<Movement> getAllMovements() {
        return movementRepository.findAllOrderByTimestampDesc();
    }

    private Money calculateBalance() {
        List<Movement> allMovements = movementRepository.findAllOrderByTimestampDesc();
        BigDecimal balance = BigDecimal.ZERO;

        for (Movement movement : allMovements) {
            switch (movement.getType()) {
                case DEPOSIT -> balance = balance.add(movement.getAmount().getAmount());
                case WITHDRAW -> balance = balance.subtract(movement.getAmount().getAmount());
            }
        }

        return Money.of(balance);
    }
}
