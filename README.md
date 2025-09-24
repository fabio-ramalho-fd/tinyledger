# Tiny Ledger API

A simple in-memory ledger API for managing financial transactions (deposits and withdrawals).

## How to Run

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Access the API:**
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **OpenAPI Docs**: http://localhost:8080/v3/api-docs

## API Endpoints

### Record a Transaction
```bash
POST /api/v1/ledger/transactions
```

**Deposit Example:**
```bash
curl -X POST http://localhost:8080/api/v1/ledger/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "DEPOSIT", "amount": "100.50"}'
```

**Withdraw Example:**
```bash
curl -X POST http://localhost:8080/api/v1/ledger/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "WITHDRAW", "amount": "25.00"}'
```

### Get Current Balance
```bash
GET /api/v1/ledger/balance
```

**Example:**
```bash
curl http://localhost:8080/api/v1/ledger/balance
```

### Get Transaction History
```bash
GET /api/v1/ledger/transactions
```

**Example:**
```bash
curl http://localhost:8080/api/v1/ledger/transactions
```

## Features

- ✅ In-memory storage (thread-safe)
- ✅ EUR currency with 2 decimal precision
- ✅ Deposit and withdrawal operations
- ✅ Real-time balance calculation
- ✅ Transaction history
- ✅ Input validation
- ✅ Swagger UI for interactive testing

## Requirements

- Java 21
- Maven 3.6+

## Quick Test

```bash
# Start the app
mvn spring-boot:run

# Make a deposit
curl -X POST http://localhost:8080/api/v1/ledger/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "DEPOSIT", "amount": "100.00"}'

# Check balance
curl http://localhost:8080/api/v1/ledger/balance

# View transactions
curl http://localhost:8080/api/v1/ledger/transactions
```