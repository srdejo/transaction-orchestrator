package com.tumipay.transaction_orchestrator.domain.model.valueobject;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Currency currency) {}
