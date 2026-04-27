package com.tumipay.transaction_orchestrator.domain.model.valueobject;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount == null) throw new IllegalArgumentException("Amount is required");
        if (currency == null) throw new IllegalArgumentException("Currency is required");
    }
}
