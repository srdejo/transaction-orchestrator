package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.command.UpdateTransactionCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTransactionService Use Case Tests")
class UpdateTransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private UpdateTransactionService updateTransactionService;

    private String txId;
    private Transaction processingTransaction;

    @BeforeEach
    void setUp() {
        txId = UUID.randomUUID().toString();
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        processingTransaction = Transaction.reconstruct(
            txId, "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PROCESSING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given existing PROCESSING transaction, when update to SUCCESS, then status changes")
    void givenExistingProcessingTransaction_whenUpdateToSuccess_thenStatusIsSuccess() {
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(processingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateTransactionCommand cmd = new UpdateTransactionCommand("SUCCESS", "PROV-123", "OK");
        Transaction result = updateTransactionService.execute(txId, cmd);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        verify(transactionRepository).save(processingTransaction);
    }

    @Test
    @DisplayName("Given existing transaction, when update to FAILED, then status is FAILED")
    void givenExistingTransaction_whenUpdateToFailed_thenStatusIsFailed() {
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(processingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateTransactionCommand cmd = new UpdateTransactionCommand("FAILED", null, "Payment declined");
        Transaction result = updateTransactionService.execute(txId, cmd);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test
    @DisplayName("Given existing PENDING transaction, when update to CANCELLED, then status is CANCELLED")
    void givenExistingTransaction_whenUpdateToCancelled_thenStatusIsCancelled() {
        // Use a PENDING transaction for cancellation
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        Transaction pendingTx = Transaction.reconstruct(
            txId, "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PENDING, LocalDateTime.now()
        );
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(pendingTx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateTransactionCommand cmd = new UpdateTransactionCommand("CANCELLED", null, "Cancelled by user");
        Transaction result = updateTransactionService.execute(txId, cmd);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.CANCELLED);
    }

    @Test
    @DisplayName("Given non-existing transaction ID, when update, then throws TransactionNotFoundException")
    void givenNonExistingId_whenUpdate_thenThrowsTransactionNotFoundException() {
        String nonExistingId = UUID.randomUUID().toString();
        when(transactionRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        UpdateTransactionCommand cmd = new UpdateTransactionCommand("SUCCESS", null, null);
        assertThatThrownBy(() -> updateTransactionService.execute(nonExistingId, cmd))
            .isInstanceOf(TransactionNotFoundException.class)
            .hasMessageContaining(nonExistingId);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given unknown status in command, when update, then transaction is saved without state change")
    void givenUnknownStatus_whenUpdate_thenSavedWithoutStateChange() {
        when(transactionRepository.findById(txId)).thenReturn(Optional.of(processingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateTransactionCommand cmd = new UpdateTransactionCommand("UNKNOWN_STATUS", null, null);
        Transaction result = updateTransactionService.execute(txId, cmd);

        // Status unchanged
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.PROCESSING);
        verify(transactionRepository).save(any());
    }
}
