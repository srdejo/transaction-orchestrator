package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.application.service.AsyncPaymentProcessor;
import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTransactionService Use Case Tests")
class CreateTransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;
    @Mock
    private AsyncPaymentProcessor asyncPaymentProcessor;
    @Mock
    private ReferenceDataPort referenceDataPort;

    @InjectMocks
    private CreateTransactionService createTransactionService;

    private CreateTransactionCommand validCommand;

    @BeforeEach
    void setUp() {
        String paymentMethodId = UUID.randomUUID().toString();
        CreateTransactionCommand.CustomerCommand customerCmd = new CreateTransactionCommand.CustomerCommand(
            "CC", "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        validCommand = new CreateTransactionCommand(
            "CLIENT-TX-001",
            BigDecimal.valueOf(10000),
            "USD",
            "CO",
            paymentMethodId,
            "https://webhook.example.com",
            "https://redirect.example.com",
            customerCmd,
            "Test payment",
            LocalDateTime.now().plusHours(1)
        );
    }

    private Transaction buildSavedTransaction(String id) {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        Money money = new Money(BigDecimal.valueOf(10000), new Currency("USD"));
        CountryCode countryCode = new CountryCode("CO");
        PaymentMethod paymentMethod = new PaymentMethod(id);
        Transaction t = new Transaction(
            "CLIENT-TX-001", money, countryCode, paymentMethod,
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null
        );
        t.assignId(UUID.randomUUID().toString());
        return t;
    }

    @Nested
    @DisplayName("Successful transaction creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("Given valid command, when execute, then transaction is saved and async processor is triggered")
        void givenValidCommand_whenExecute_thenSavesTransactionAndTriggersAsync() {
            // Arrange
            when(referenceDataPort.isValidCountry("CO")).thenReturn(true);
            when(referenceDataPort.isValidCurrency("USD")).thenReturn(true);
            when(referenceDataPort.isValidPaymentMethod(validCommand.paymentMethodId())).thenReturn(true);
            Transaction saved = buildSavedTransaction(validCommand.paymentMethodId());
            when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

            // Act
            Transaction result = createTransactionService.execute(validCommand);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            verify(transactionRepository, times(1)).save(any(Transaction.class));
            verify(asyncPaymentProcessor, times(1)).process(any(Transaction.class));
        }

        @Test
        @DisplayName("Given valid command, when execute, then returned transaction is in PENDING state")
        void givenValidCommand_whenExecute_thenReturnedTransactionIsInPendingState() {
            // Arrange
            when(referenceDataPort.isValidCountry(any())).thenReturn(true);
            when(referenceDataPort.isValidCurrency(any())).thenReturn(true);
            when(referenceDataPort.isValidPaymentMethod(any())).thenReturn(true);
            Transaction saved = buildSavedTransaction(validCommand.paymentMethodId());
            when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

            // Act
            Transaction result = createTransactionService.execute(validCommand);

            // Assert
            assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("Reference data validation")
    class ReferenceDataValidation {

        @Test
        @DisplayName("Given invalid country code, when execute, then throws BusinessException with INVALID_COUNTRY code")
        void givenInvalidCountry_whenExecute_thenThrowsBusinessException_INVALID_COUNTRY() {
            when(referenceDataPort.isValidCountry("CO")).thenReturn(false);

            assertThatThrownBy(() -> createTransactionService.execute(validCommand))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_COUNTRY));

            verify(transactionRepository, never()).save(any());
            verify(asyncPaymentProcessor, never()).process(any());
        }

        @Test
        @DisplayName("Given invalid currency, when execute, then throws BusinessException with INVALID_CURRENCY code")
        void givenInvalidCurrency_whenExecute_thenThrowsBusinessException_INVALID_CURRENCY() {
            when(referenceDataPort.isValidCountry("CO")).thenReturn(true);
            when(referenceDataPort.isValidCurrency("USD")).thenReturn(false);

            assertThatThrownBy(() -> createTransactionService.execute(validCommand))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_CURRENCY));

            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Given invalid payment method, when execute, then throws BusinessException with INVALID_PAYMENT_METHOD code")
        void givenInvalidPaymentMethod_whenExecute_thenThrowsBusinessException_INVALID_PAYMENT_METHOD() {
            when(referenceDataPort.isValidCountry("CO")).thenReturn(true);
            when(referenceDataPort.isValidCurrency("USD")).thenReturn(true);
            when(referenceDataPort.isValidPaymentMethod(any())).thenReturn(false);

            assertThatThrownBy(() -> createTransactionService.execute(validCommand))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_PAYMENT_METHOD));

            verify(transactionRepository, never()).save(any());
        }
    }
}
