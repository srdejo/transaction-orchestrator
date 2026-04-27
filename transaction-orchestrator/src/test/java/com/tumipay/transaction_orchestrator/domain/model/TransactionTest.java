package com.tumipay.transaction_orchestrator.domain.model;

import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Transaction Domain Model Tests")
class TransactionTest {

    private Transaction transaction;
    private Customer customer;
    private Money money;
    private CountryCode countryCode;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        customer = new Customer(
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
        money = new Money(BigDecimal.valueOf(10000), new Currency("USD"));
        countryCode = new CountryCode("CO");
        paymentMethod = new PaymentMethod(UUID.randomUUID().toString());

        transaction = new Transaction(
            "CLIENT-TX-001",
            money,
            countryCode,
            paymentMethod,
            "https://webhook.example.com",
            "https://redirect.example.com",
            customer,
            "Test payment",
            LocalDateTime.now().plusHours(1)
        );
    }

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("Given valid data, when creating a transaction, then status is PENDING")
        void givenValidData_whenCreateTransaction_thenStatusIsPending() {
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
            assertThat(transaction.getClientTransactionId()).isEqualTo("CLIENT-TX-001");
            assertThat(transaction.getAmount()).isEqualTo(money);
            assertThat(transaction.getCustomer()).isEqualTo(customer);
        }

        @Test
        @DisplayName("Given null clientTransactionId, when creating, then throws IllegalArgumentException")
        void givenNullClientTransactionId_whenCreate_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> new Transaction(
                null, money, countryCode, paymentMethod,
                "https://webhook.example.com", "https://redirect.example.com",
                customer, "desc", null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("ClientTransactionId is required");
        }

        @Test
        @DisplayName("Given blank clientTransactionId, when creating, then throws IllegalArgumentException")
        void givenBlankClientTransactionId_whenCreate_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> new Transaction(
                "   ", money, countryCode, paymentMethod,
                "https://webhook.example.com", "https://redirect.example.com",
                customer, "desc", null
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Given null amount, when creating, then throws IllegalArgumentException")
        void givenNullAmount_whenCreate_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> new Transaction(
                "CLIENT-TX-002", null, countryCode, paymentMethod,
                "https://webhook.example.com", "https://redirect.example.com",
                customer, "desc", null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Amount is required");
        }

        @Test
        @DisplayName("Given null webhookUrl, when creating, then throws IllegalArgumentException")
        void givenNullWebhookUrl_whenCreate_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> new Transaction(
                "CLIENT-TX-003", money, countryCode, paymentMethod,
                null, "https://redirect.example.com",
                customer, "desc", null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("WebhookUrl is required");
        }

        @Test
        @DisplayName("Given null customer, when creating, then throws IllegalArgumentException")
        void givenNullCustomer_whenCreate_thenThrowsIllegalArgument() {
            assertThatThrownBy(() -> new Transaction(
                "CLIENT-TX-004", money, countryCode, paymentMethod,
                "https://webhook.example.com", "https://redirect.example.com",
                null, "desc", null
            )).isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("Customer is required");
        }

        @Test
        @DisplayName("Given valid transaction, when created, then createdAt is set")
        void givenValidTransaction_whenCreated_thenCreatedAtIsSet() {
            assertThat(transaction.getCreatedAt()).isNotNull();
            assertThat(transaction.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("ID Assignment")
    class IdAssignment {

        @Test
        @DisplayName("Given no ID assigned, when assignId, then ID is set")
        void givenNoId_whenAssignId_thenIdIsSet() {
            String id = UUID.randomUUID().toString();
            transaction.assignId(id);
            assertThat(transaction.getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("Given ID already assigned, when assignId again, then throws IllegalStateException")
        void givenAssignedId_whenAssignIdAgain_thenThrowsIllegalState() {
            transaction.assignId(UUID.randomUUID().toString());
            assertThatThrownBy(() -> transaction.assignId(UUID.randomUUID().toString()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Transaction ID is already assigned");
        }
    }

    @Nested
    @DisplayName("State Machine")
    class StateMachine {

        @Test
        @DisplayName("Given PENDING transaction, when process, then status is PROCESSING")
        void givenPendingTransaction_whenProcess_thenStatusIsProcessing() {
            transaction.assignId(UUID.randomUUID().toString());
            transaction.process();
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PROCESSING);
        }

        @Test
        @DisplayName("Given PROCESSING transaction, when complete, then status is SUCCESS")
        void givenProcessingTransaction_whenComplete_thenStatusIsSuccess() {
            transaction.assignId(UUID.randomUUID().toString());
            transaction.process();
            transaction.complete();
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        }

        @Test
        @DisplayName("Given PENDING transaction, when complete directly, then throws IllegalStateException")
        void givenPendingTransaction_whenComplete_thenThrowsIllegalState() {
            assertThatThrownBy(() -> transaction.complete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only PROCESSING transactions can be completed");
        }

        @Test
        @DisplayName("Given SUCCESS transaction, when cancel, then throws IllegalStateException")
        void givenSuccessTransaction_whenCancel_thenThrowsIllegalState() {
            transaction.assignId(UUID.randomUUID().toString());
            transaction.process();
            transaction.complete();
            assertThatThrownBy(() -> transaction.cancel())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SUCCESS transactions cannot be cancelled");
        }

        @Test
        @DisplayName("Given any transaction, when fail, then status is FAILED")
        void givenAnyTransaction_whenFail_thenStatusIsFailed() {
            transaction.assignId(UUID.randomUUID().toString());
            transaction.fail();
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
        }

        @Test
        @DisplayName("Given PENDING transaction, when cancel, then status is CANCELLED")
        void givenPendingTransaction_whenCancel_thenStatusIsCancelled() {
            transaction.cancel();
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.CANCELLED);
        }

        @Test
        @DisplayName("Given non-PENDING transaction, when process again, then throws IllegalStateException")
        void givenNonPendingTransaction_whenProcess_thenThrowsIllegalState() {
            transaction.assignId(UUID.randomUUID().toString());
            transaction.process();
            assertThatThrownBy(() -> transaction.process())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only PENDING transactions can be processed");
        }
    }

    @Nested
    @DisplayName("Reconstruct")
    class Reconstruct {

        @Test
        @DisplayName("Given stored data, when reconstruct, then all fields are populated correctly")
        void givenStoredData_whenReconstruct_thenFieldsArePopulated() {
            String id = UUID.randomUUID().toString();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

            Transaction reconstructed = Transaction.reconstruct(
                id, "CLIENT-TX-001", money, countryCode, paymentMethod,
                "https://webhook.example.com", "https://redirect.example.com",
                customer, "Test", null, TransactionStatus.SUCCESS, createdAt
            );

            assertThat(reconstructed.getId()).isEqualTo(id);
            assertThat(reconstructed.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(reconstructed.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}
