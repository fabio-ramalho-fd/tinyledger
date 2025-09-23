package com.example.ledger.repo;

import com.example.ledger.domain.Movement;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Repository
public class InMemoryMovementRepository {

    private final List<Movement> movements = new ArrayList<>();

    public synchronized Movement save(Movement movement) {
        Objects.requireNonNull(movement, "Movement cannot be null");
        movements.add(movement);
        return movement;
    }

    public synchronized List<Movement> findAllOrderByTimestampDesc() {
        List<Movement> result = new ArrayList<>(movements);
        result.sort(Comparator.comparing(Movement::getCreatedAt).reversed());
        return result;
    }
}
