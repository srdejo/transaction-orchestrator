package com.tumipay.transaction_orchestrator;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Helper factory for building common test fixtures, shared across all test classes.
 */
public final class TestFixtures {

    private TestFixtures() {}

    public static Customer buildCustomer() {
        return new Customer(
            DocumentType.CC,
            "12345678",
            "+57",
            "3001234567",
            "john.doe@example.com",
            "John",
            null,
            "Doe",
            null
        );
    }

    public static Transaction buildTransaction(String txId, long amountCents, TransactionStatus status) {
        return Transaction.reconstruct(
            txId,
            "CLIENT-TX-" + txId.substring(0, 8),
            new Money(BigDecimal.valueOf(amountCents), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com",
            "https://redirect.example.com",
            buildCustomer(),
            "Test payment",
            null,
            status,
            LocalDateTime.now()
        );
    }

    public static Transaction buildPendingTransaction() {
        return buildTransaction(UUID.randomUUID().toString(), 10000, TransactionStatus.PENDING);
    }

    public static Transaction buildProcessingTransaction(String txId) {
        return buildTransaction(txId, 10000, TransactionStatus.PROCESSING);
    }
}
