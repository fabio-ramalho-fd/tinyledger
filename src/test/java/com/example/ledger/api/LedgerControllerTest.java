package com.example.ledger.api;

import com.example.ledger.domain.LedgerService;
import com.example.ledger.domain.Money;
import com.example.ledger.domain.Transaction;
import com.example.ledger.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerService ledgerService;

    private final Instant fixedInstant = Instant.parse("2023-10-01T10:15:30.123Z");

    @Test
    void shouldCreateDepositTransaction() throws Exception {
        // given
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, Money.of("100.50"), fixedInstant);
        when(ledgerService.deposit(any(Money.class))).thenReturn(transaction);

        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": 100.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transaction.getId().toString()))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.createdAt").value("2023-10-01T10:15:30.123Z"));
    }

    @Test
    void shouldCreateWithdrawTransaction() throws Exception {
        // given
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, Money.of("50.25"), fixedInstant);
        when(ledgerService.withdraw(any(Money.class))).thenReturn(transaction);

        String requestBody = """
            {
                "type": "WITHDRAW",
                "amount": 50.25
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transaction.getId().toString()))
                .andExpect(jsonPath("$.type").value("WITHDRAW"))
                .andExpect(jsonPath("$.amount").value(50.25))
                .andExpect(jsonPath("$.createdAt").value("2023-10-01T10:15:30.123Z"));
    }

    @Test
    void shouldRejectTransactionWithNullType() throws Exception {
        // given
        String requestBody = """
            {
                "type": null,
                "amount": 100.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTransactionWithNullAmount() throws Exception {
        // given
        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": null
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTransactionWithNegativeAmount() throws Exception {
        // given
        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": -10.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTransactionWithZeroAmount() throws Exception {
        // given
        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": 0.00
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTransactionWithMoreThanTwoDecimals() throws Exception {
        // given
        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": 100.123
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTransactionWithInvalidType() throws Exception {
        // given
        String requestBody = """
            {
                "type": "INVALID",
                "amount": 100.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleInsufficientFundsError() throws Exception {
        // given
        when(ledgerService.withdraw(any(Money.class)))
            .thenThrow(new IllegalArgumentException("Insufficient funds: current balance is 50.00, requested 100.00"));

        String requestBody = """
            {
                "type": "WITHDRAW",
                "amount": 100.00
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Insufficient funds: current balance is 50.00, requested 100.00"));
    }

    @Test
    void shouldGetCurrentBalance() throws Exception {
        // given
        Money balance = Money.of("150.75");
        when(ledgerService.getBalance()).thenReturn(balance);

        // when & then
        mockMvc.perform(get("/api/v1/ledger/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.75));
    }

    @Test
    void shouldGetZeroBalance() throws Exception {
        // given
        Money balance = Money.of("0.00");
        when(ledgerService.getBalance()).thenReturn(balance);

        // when & then
        mockMvc.perform(get("/api/v1/ledger/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void shouldGetTransactionHistory() throws Exception {
        // given
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, Money.of("100.00"), fixedInstant);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAW, Money.of("25.50"), fixedInstant.plusSeconds(60));
        List<Transaction> transactions = Arrays.asList(transaction2, transaction1); // Most recent first

        when(ledgerService.getAllTransactions()).thenReturn(transactions);

        // when & then
        mockMvc.perform(get("/api/v1/ledger/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(transaction2.getId().toString()))
                .andExpect(jsonPath("$[0].type").value("WITHDRAW"))
                .andExpect(jsonPath("$[0].amount").value(25.50))
                .andExpect(jsonPath("$[1].id").value(transaction1.getId().toString()))
                .andExpect(jsonPath("$[1].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(100.00));
    }

    @Test
    void shouldGetEmptyTransactionHistory() throws Exception {
        // given
        when(ledgerService.getAllTransactions()).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/v1/ledger/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldHandleLowercaseTransactionType() throws Exception {
        // given
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, Money.of("100.50"), fixedInstant);
        when(ledgerService.deposit(any(Money.class))).thenReturn(transaction);

        String requestBody = """
            {
                "type": "deposit",
                "amount": 100.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"));
    }

    @Test
    void shouldRejectEmptyRequestBody() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        // given
        String malformedJson = """
            {
                "type": "DEPOSIT",
                "amount": 100.50
            """; // Missing closing brace

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    void shouldRejectMissingContentType() throws Exception {
        // given
        String requestBody = """
            {
                "type": "DEPOSIT",
                "amount": 100.50
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/ledger/transactions")
                .content(requestBody)) // No content type
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }
}
