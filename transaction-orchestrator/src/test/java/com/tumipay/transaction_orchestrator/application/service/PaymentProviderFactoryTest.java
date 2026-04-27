package com.tumipay.transaction_orchestrator.application.service;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider.FailingHttpProviderAdapter;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider.SuccessfulHttpProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PaymentProviderFactory Tests")
class PaymentProviderFactoryTest {

    private PaymentProviderFactory factory;
    private SuccessfulHttpProviderAdapter successfulProvider;
    private FailingHttpProviderAdapter failingProvider;

    @BeforeEach
    void setUp() {
        successfulProvider = new SuccessfulHttpProviderAdapter();
        failingProvider = new FailingHttpProviderAdapter();
        factory = new PaymentProviderFactory(List.of(successfulProvider, failingProvider));
    }

    private Transaction buildTransaction(long amountCents) {
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
            TransactionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given amount NOT ending in 99, when getProvider, then returns SuccessfulHttpProviderAdapter")
    void givenAmountNotEndingIn99_whenGetProvider_thenReturnsSuccessfulProvider() {
        Transaction tx = buildTransaction(10000); // 10000 % 100 = 0, not 99

        PaymentProviderPort provider = factory.getProvider(tx);

        assertThat(provider).isInstanceOf(SuccessfulHttpProviderAdapter.class);
    }

    @Test
    @DisplayName("Given amount ending in 99, when getProvider, then returns FailingHttpProviderAdapter")
    void givenAmountEndingIn99_whenGetProvider_thenReturnsFailingProvider() {
        Transaction tx = buildTransaction(9999); // 9999 % 100 = 99

        PaymentProviderPort provider = factory.getProvider(tx);

        assertThat(provider).isInstanceOf(FailingHttpProviderAdapter.class);
    }

    @Test
    @DisplayName("Given no provider supports the transaction, when getProvider, then throws IllegalArgumentException")
    void givenNoSupportedProvider_whenGetProvider_thenThrowsIllegalArgument() {
        // Create factory with no providers
        PaymentProviderFactory emptyFactory = new PaymentProviderFactory(List.of());
        Transaction tx = buildTransaction(10000);

        assertThatThrownBy(() -> emptyFactory.getProvider(tx))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported payment method");
    }
}
