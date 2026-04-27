package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FailingHttpProviderAdapter Tests")
class FailingHttpProviderAdapterTest {

    private FailingHttpProviderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FailingHttpProviderAdapter();
    }

    private Transaction buildTransaction(long amountCents, TransactionStatus status) {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        return Transaction.reconstruct(
            UUID.randomUUID().toString(), "CLIENT-TX",
            new Money(BigDecimal.valueOf(amountCents), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test", null,
            status, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given amount ending in 99, when supports, then returns true")
    void givenAmountEndingIn99_whenSupports_thenReturnsTrue() {
        Transaction tx = buildTransaction(9999, TransactionStatus.PENDING);
        assertThat(adapter.supports(tx)).isTrue();
    }

    @Test
    @DisplayName("Given amount NOT ending in 99, when supports, then returns false")
    void givenAmountNotEndingIn99_whenSupports_thenReturnsFalse() {
        Transaction tx = buildTransaction(10000, TransactionStatus.PENDING);
        assertThat(adapter.supports(tx)).isFalse();
    }

    @Test
    @DisplayName("Given PROCESSING transaction, when processPayment, then status is FAILED")
    void givenProcessingTransaction_whenProcessPayment_thenTransactionIsFailed() {
        Transaction tx = buildTransaction(9999, TransactionStatus.PROCESSING);

        Transaction result = adapter.processPayment(tx);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
    }
}
