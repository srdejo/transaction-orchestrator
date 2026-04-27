package com.tumipay.transaction_orchestrator.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Given valid amount and currency, when create, then object is initialized")
    void givenValidData_whenCreate_thenInitialized() {
        BigDecimal amount = new BigDecimal("100.50");
        Currency currency = new Currency("USD");
        
        Money money = new Money(amount, currency);
        
        assertThat(money.amount()).isEqualTo(amount);
        assertThat(money.currency()).isEqualTo(currency);
    }

    @Test
    @DisplayName("Given null amount, when create, then throws exception")
    void givenNullAmount_whenCreate_thenThrowsException() {
        assertThatThrownBy(() -> new Money(null, new Currency("USD")))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Given null currency, when create, then throws exception")
    void givenNullCurrency_whenCreate_thenThrowsException() {
        assertThatThrownBy(() -> new Money(BigDecimal.TEN, null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
