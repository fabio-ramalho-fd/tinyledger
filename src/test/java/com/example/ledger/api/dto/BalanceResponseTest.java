package com.example.ledger.api.dto;

import com.example.ledger.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class BalanceResponseTest {

    @Test
    void shouldCreateFromMoney() {
        // given
        Money money = Money.of("150.75");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(money.getAmount(), response.balance());
    }

    @Test
    void shouldCreateFromZeroMoney() {
        // given
        Money money = Money.of("0.00");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), response.balance());
    }

    @Test
    void shouldPreserveMoneyAmount() {
        // given
        Money money = Money.of("999.99");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(new BigDecimal("999.99"), response.balance());
    }

    @Test
    void shouldPreserveDecimalPrecision() {
        // given
        Money money = Money.of("123.45");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals("123.45", response.balance().toString());
    }

    @Test
    void shouldHandleSmallAmounts() {
        // given
        Money money = Money.of("0.01");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(new BigDecimal("0.01"), response.balance());
    }

    @Test
    void shouldHandleLargeAmounts() {
        // given
        Money money = Money.of("1000000.00");
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(new BigDecimal("1000000.00"), response.balance());
    }

    @Test
    void shouldCreateResponseWithCorrectScale() {
        // given
        Money money = Money.of("100.5"); // Will be normalized to 100.50
        
        // when
        BalanceResponse response = BalanceResponse.from(money);
        
        // then
        assertEquals(2, response.balance().scale());
        assertEquals(new BigDecimal("100.50"), response.balance());
    }
}
