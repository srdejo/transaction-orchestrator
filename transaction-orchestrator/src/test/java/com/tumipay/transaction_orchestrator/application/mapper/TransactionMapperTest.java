package com.tumipay.transaction_orchestrator.application.mapper;

import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.application.ports.in.command.UpdateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TransactionMapper Tests")
class TransactionMapperTest {

    private TransactionMapper mapper;
    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = mock(CustomerMapper.class);
        mapper = new TransactionMapper(customerMapper);
    }

    private Transaction buildDomainTransaction() {
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        String txId = UUID.randomUUID().toString();
        String pmId = UUID.randomUUID().toString();
        return Transaction.reconstruct(
            txId, "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(pmId),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Given a domain Transaction, when toResponse, then wrapper has correct code and message")
    void givenDomainTransaction_whenToResponse_thenWrapperCodeAndMessageAreCorrect() {
        Transaction transaction = buildDomainTransaction();

        var response = mapper.toResponse(transaction);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("000");
        assertThat(response.getMessage()).isEqualTo("Successful operation");
        assertThat(response.getData()).isNotNull();
    }

    @Test
    @DisplayName("Given a domain Transaction, when toResponse, then data contains correct transaction fields")
    void givenDomainTransaction_whenToResponse_thenDataHasCorrectFields() {
        Transaction transaction = buildDomainTransaction();

        var response = mapper.toResponse(transaction);
        var data = response.getData();

        assertThat(data.getTransactionId().toString()).isEqualTo(transaction.getId());
        assertThat(data.getClientTransactionId()).isEqualTo("CLIENT-TX-001");
        assertThat(data.getCurrency()).isEqualTo("USD");
        assertThat(data.getCountry()).isEqualTo("CO");
        assertThat(data.getAmount()).isEqualTo(10000);
    }

    @Test
    @DisplayName("Given UpdateTransactionRequest with FAILED status, when toCommand, then command has FAILED status")
    void givenUpdateRequest_whenToCommand_thenStatusMappedCorrectly() {
        com.tumipay.transaction_orchestrator.api.model.UpdateTransactionRequest request =
            new com.tumipay.transaction_orchestrator.api.model.UpdateTransactionRequest();
        request.setStatus(com.tumipay.transaction_orchestrator.api.model.TransactionStatus.FAILED);
        request.setProviderTransactionId("PROV-XYZ");
        request.setMessage("Payment declined");

        UpdateTransactionCommand cmd = mapper.toCommand(request);

        assertThat(cmd.status()).isEqualTo("FAILED");
        assertThat(cmd.providerTransactionId()).isEqualTo("PROV-XYZ");
        assertThat(cmd.message()).isEqualTo("Payment declined");
    }

    @Test
    @DisplayName("Given CreateTransactionRequest, when toCommand, then delegates customer mapping to CustomerMapper")
    void givenCreateRequest_whenToCommand_thenDelegatesToCustomerMapper() {
        when(customerMapper.toCommand(any())).thenReturn(
            new CreateTransactionCommand.CustomerCommand("CC", "12345678", "+57", "3001234567",
                "john@test.com", "John", null, "Doe", null)
        );

        com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest request =
            new com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest();
        request.setClientTransactionId("CLIENT-TX-001");
        request.setAmount(100);
        request.setCurrency("USD");
        request.setCountry("CO");
        request.setCustomer(new com.tumipay.transaction_orchestrator.api.model.Customer());

        CreateTransactionCommand cmd = mapper.toCommand(request);

        assertThat(cmd.clientTransactionId()).isEqualTo("CLIENT-TX-001");
        assertThat(cmd.currency()).isEqualTo("USD");
        assertThat(cmd.countryCode()).isEqualTo("CO");
        assertThat(cmd.amount()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        assertThat(cmd.customer()).isNotNull();
    }
}
