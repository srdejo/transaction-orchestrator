package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FailingHttpProviderAdapter Tests")
class FailingHttpProviderAdapterTest {

    private FailingHttpProviderAdapter adapter;
    private ReferenceDataPort referenceDataPort;

    @BeforeEach
    void setUp() {
        referenceDataPort = mock(ReferenceDataPort.class);
        adapter = new FailingHttpProviderAdapter(referenceDataPort);
    }

    private Transaction buildTransaction(String paymentMethodId, TransactionStatus status) {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        return Transaction.reconstruct(
            UUID.randomUUID().toString(), "CLIENT-TX",
            10000L,
            new Currency("USD"),
            new CountryCode("CO"),
            new PaymentMethod(paymentMethodId),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test", null,
            status, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given CARD payment method, when supports, then returns true")
    void givenCardPaymentMethod_whenSupports_thenReturnsTrue() {
        String cardId = "550e8400-e29b-41d4-a716-446655440001";
        Transaction tx = buildTransaction(cardId, TransactionStatus.PENDING);
        when(referenceDataPort.isCardPaymentMethod(cardId)).thenReturn(true);

        assertThat(adapter.supports(tx)).isTrue();
    }

    @Test
    @DisplayName("Given non-CARD payment method, when supports, then returns false")
    void givenNonCardPaymentMethod_whenSupports_thenReturnsFalse() {
        String otherId = "550e8400-e29b-41d4-a716-446655440002";
        Transaction tx = buildTransaction(otherId, TransactionStatus.PENDING);
        when(referenceDataPort.isCardPaymentMethod(otherId)).thenReturn(false);

        assertThat(adapter.supports(tx)).isFalse();
    }

    @Test
    @DisplayName("Given PROCESSING transaction, when processPayment, then status is FAILED")
    void givenProcessingTransaction_whenProcessPayment_thenTransactionIsFailed() {
        Transaction tx = buildTransaction("550e8400-e29b-41d4-a716-446655440001", TransactionStatus.PROCESSING);

        Transaction result = adapter.processPayment(tx);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(result.getProviderResponse()).contains("insufficient_funds");
    }
}
