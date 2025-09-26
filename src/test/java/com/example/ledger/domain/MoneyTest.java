package com.example.ledger.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithValidAmount() {
        // given
        BigDecimal amount = new BigDecimal("10.50");
        
        // when
        Money money = Money.of(amount);
        
        // then
        assertEquals(amount, money.getAmount());
    }

    @Test
    void shouldCreateMoneyFromString() {
        // given
        String amount = "15.75";
        
        // when
        Money money = Money.of(amount);
        
        // then
        assertEquals(new BigDecimal("15.75"), money.getAmount());
    }

    @Test
    void shouldAcceptZeroAmount() {
        // given
        BigDecimal amount = BigDecimal.ZERO;
        
        // when
        Money money = Money.of(amount);
        
        // then
        assertEquals(new BigDecimal("0.00"), money.getAmount());
    }

    @Test
    void shouldRejectNegativeAmount() {
        // given
        BigDecimal negativeAmount = new BigDecimal("-10.00");
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Money.of(negativeAmount));
    }

    @Test
    void shouldRejectNullAmount() {
        // when & then
        assertThrows(NullPointerException.class, () -> Money.of((BigDecimal) null));
    }

    @Test
    void shouldRejectNullStringAmount() {
        // when & then
        assertThrows(NullPointerException.class, () -> Money.of((String) null));
    }

    @Test
    void shouldRejectEmptyStringAmount() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Money.of(""));
        assertThrows(IllegalArgumentException.class, () -> Money.of("   "));
    }

    @Test
    void shouldRejectAmountWithMoreThanTwoDecimals() {
        // given
        BigDecimal amount = new BigDecimal("10.123");
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Money.of(amount));
    }

    @Test
    void shouldAddTwoMoneyAmounts() {
        // given
        Money money1 = Money.of("10.50");
        Money money2 = Money.of("5.25");
        
        // when
        Money result = money1.add(money2);
        
        // then
        assertEquals(new BigDecimal("15.75"), result.getAmount());
    }

    @Test
    void shouldRejectAddingNullMoney() {
        // given
        Money money = Money.of("10.00");
        
        // when & then
        assertThrows(NullPointerException.class, () -> money.add(null));
    }

    @Test
    void shouldRoundAmountToTwoDecimals() {
        // given
        BigDecimal amount = new BigDecimal("10.1");
        
        // when
        Money money = Money.of(amount);
        
        // then
        assertEquals(new BigDecimal("10.10"), money.getAmount());
    }

    @Test
    void shouldBeEqualWhenAmountsAreEqual() {
        // given
        Money money1 = Money.of("10.50");
        Money money2 = Money.of("10.50");
        
        // when & then
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenAmountsAreDifferent() {
        // given
        Money money1 = Money.of("10.50");
        Money money2 = Money.of("10.51");
        
        // when & then
        assertNotEquals(money1, money2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        // given
        Money money = Money.of("10.50");
        
        // when & then
        assertNotEquals(null, money);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        // given
        Money money = Money.of("10.50");
        String other = "10.50";
        
        // when & then
        assertNotEquals(other, money);
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        // given
        Money money = Money.of("10.50");
        
        // when
        String result = money.toString();
        
        // then
        assertEquals("10.50", result);
    }
}
