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
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsyncPaymentProcessor Service Tests")
class AsyncPaymentProcessorTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;
    @Mock
    private PaymentProviderFactory paymentProviderFactory;
    @Mock
    private PaymentProviderPort paymentProviderPort;

    @InjectMocks
    private AsyncPaymentProcessor asyncPaymentProcessor;

    private Transaction pendingTransaction;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        pendingTransaction = Transaction.reconstruct(
            UUID.randomUUID().toString(), "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given successful provider, when process, then transaction is SUCCESS and saved twice")
    void givenSuccessfulProvider_whenProcess_thenTransactionIsSuccessAndSavedTwice() {
        // Arrange: provider sets status to PROCESSING then SUCCESS
        when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
        when(paymentProviderPort.processPayment(any())).thenAnswer(inv -> {
            Transaction tx = inv.getArgument(0);
            tx.complete();
            return tx;
        });
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act (process is normally @Async, but in tests it runs synchronously)
        asyncPaymentProcessor.process(pendingTransaction);

        // Assert
        assertThat(pendingTransaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        // save called at least twice: once for PROCESSING state, once for SUCCESS state
        verify(transactionRepository, atLeast(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Given provider that throws exception, when process, then transaction is FAILED and saved")
    void givenProviderThrowsException_whenProcess_thenTransactionIsFailedAndSaved() {
        // Arrange
        when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
        when(paymentProviderPort.processPayment(any())).thenThrow(new RuntimeException("Provider connection timeout"));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        asyncPaymentProcessor.process(pendingTransaction);

        // Assert
        assertThat(pendingTransaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
        verify(transactionRepository, atLeast(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Given factory that throws exception, when process, then transaction is FAILED")
    void givenFactoryThrowsException_whenProcess_thenTransactionIsFailed() {
        // Arrange
        when(paymentProviderFactory.getProvider(any())).thenThrow(new IllegalArgumentException("Unsupported payment method"));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        asyncPaymentProcessor.process(pendingTransaction);

        // Assert
        assertThat(pendingTransaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
        verify(transactionRepository, times(1)).save(pendingTransaction);
    }
}
