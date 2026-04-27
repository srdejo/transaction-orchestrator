package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import com.tumipay.transaction_orchestrator.infrastructure.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTransactionService Use Case Tests")
class GetTransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private GetTransactionService getTransactionService;

    private Transaction existingTransaction;
    private String existingId;

    @BeforeEach
    void setUp() {
        existingId = UUID.randomUUID().toString();
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        existingTransaction = Transaction.reconstruct(
            existingId, "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given existing transaction ID, when execute, then returns the transaction")
    void givenExistingId_whenExecute_thenReturnsTransaction() {
        when(transactionRepository.findById(existingId)).thenReturn(Optional.of(existingTransaction));

        Transaction result = getTransactionService.execute(existingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingId);
        assertThat(result.getClientTransactionId()).isEqualTo("CLIENT-TX-001");
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
    }

    @Test
    @DisplayName("Given non-existing transaction ID, when execute, then throws TransactionNotFoundException")
    void givenNonExistingId_whenExecute_thenThrowsTransactionNotFoundException() {
        String nonExistingId = UUID.randomUUID().toString();
        when(transactionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getTransactionService.execute(nonExistingId))
            .isInstanceOf(TransactionNotFoundException.class)
            .hasMessageContaining(nonExistingId);
    }
}
