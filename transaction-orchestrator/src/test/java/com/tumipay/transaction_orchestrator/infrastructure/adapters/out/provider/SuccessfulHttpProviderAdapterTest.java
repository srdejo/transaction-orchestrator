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
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import static org.mockito.Mockito.*;

@DisplayName("SuccessfulHttpProviderAdapter Tests")
class SuccessfulHttpProviderAdapterTest {

    private SuccessfulHttpProviderAdapter adapter;
    private ReferenceDataPort referenceDataPort;

    @BeforeEach
    void setUp() {
        referenceDataPort = mock(ReferenceDataPort.class);
        adapter = new SuccessfulHttpProviderAdapter(referenceDataPort);
    }

    private Transaction buildTransaction(String paymentMethodId, TransactionStatus status) {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        return Transaction.reconstruct(
            UUID.randomUUID().toString(), "CLIENT-TX",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(paymentMethodId),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test", null,
            status, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given non-CARD payment method, when supports, then returns true")
    void givenNonCardPaymentMethod_whenSupports_thenReturnsTrue() {
        String otherId = "550e8400-e29b-41d4-a716-446655440002";
        Transaction tx = buildTransaction(otherId, TransactionStatus.PENDING);
        when(referenceDataPort.isCardPaymentMethod(otherId)).thenReturn(false);

        assertThat(adapter.supports(tx)).isTrue();
    }

    @Test
    @DisplayName("Given CARD payment method, when supports, then returns false")
    void givenCardPaymentMethod_whenSupports_thenReturnsFalse() {
        String cardId = "550e8400-e29b-41d4-a716-446655440001";
        Transaction tx = buildTransaction(cardId, TransactionStatus.PENDING);
        when(referenceDataPort.isCardPaymentMethod(cardId)).thenReturn(true);

        assertThat(adapter.supports(tx)).isFalse();
    }

    @Test
    @DisplayName("Given PROCESSING transaction, when processPayment, then status is SUCCESS")
    void givenProcessingTransaction_whenProcessPayment_thenTransactionIsSuccess() {
        Transaction tx = buildTransaction("550e8400-e29b-41d4-a716-446655440002", TransactionStatus.PROCESSING);

        Transaction result = adapter.processPayment(tx);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(result.getProviderResponse()).contains("approved");
    }
}
