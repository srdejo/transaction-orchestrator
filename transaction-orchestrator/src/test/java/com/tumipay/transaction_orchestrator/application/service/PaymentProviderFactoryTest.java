package com.tumipay.transaction_orchestrator.application.service;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider.FailingHttpProviderAdapter;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider.SuccessfulHttpProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PaymentProviderFactory Tests")
class PaymentProviderFactoryTest {

    private PaymentProviderFactory factory;
    private SuccessfulHttpProviderAdapter successfulProvider;
    private FailingHttpProviderAdapter failingProvider;
    private ReferenceDataPort referenceDataPort;

    private static final String CARD_ID = "550e8400-e29b-41d4-a716-446655440001";
    private static final String OTHER_ID = "550e8400-e29b-41d4-a716-446655440002";

    @BeforeEach
    void setUp() {
        referenceDataPort = mock(ReferenceDataPort.class);
        successfulProvider = new SuccessfulHttpProviderAdapter(referenceDataPort);
        failingProvider = new FailingHttpProviderAdapter(referenceDataPort);
        factory = new PaymentProviderFactory(List.of(successfulProvider, failingProvider));
    }

    private Transaction buildTransaction(String paymentMethodId) {
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
            TransactionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given payment method is NOT CARD, when getProvider, then returns SuccessfulHttpProviderAdapter")
    void givenPaymentMethodNotCard_whenGetProvider_thenReturnsSuccessfulProvider() {
        Transaction tx = buildTransaction(OTHER_ID);
        when(referenceDataPort.isCardPaymentMethod(OTHER_ID)).thenReturn(false);

        PaymentProviderPort provider = factory.getProvider(tx);

        assertThat(provider).isInstanceOf(SuccessfulHttpProviderAdapter.class);
    }

    @Test
    @DisplayName("Given payment method IS CARD, when getProvider, then returns FailingHttpProviderAdapter")
    void givenPaymentMethodCard_whenGetProvider_thenReturnsFailingProvider() {
        Transaction tx = buildTransaction(CARD_ID);
        when(referenceDataPort.isCardPaymentMethod(CARD_ID)).thenReturn(true);

        PaymentProviderPort provider = factory.getProvider(tx);

        assertThat(provider).isInstanceOf(FailingHttpProviderAdapter.class);
    }

    @Test
    @DisplayName("Given no provider supports the transaction, when getProvider, then throws BusinessException")
    void givenNoSupportedProvider_whenGetProvider_thenThrowsBusinessException() {
        // Create factory with no providers
        PaymentProviderFactory emptyFactory = new PaymentProviderFactory(List.of());
        Transaction tx = buildTransaction(OTHER_ID);

        assertThatThrownBy(() -> emptyFactory.getProvider(tx))
            .isInstanceOf(com.tumipay.transaction_orchestrator.domain.exception.BusinessException.class)
            .hasMessageContaining("error.unsupported.payment.method");
    }
}
