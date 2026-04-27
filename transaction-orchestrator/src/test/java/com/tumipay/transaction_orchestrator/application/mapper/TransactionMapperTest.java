package com.tumipay.transaction_orchestrator.application.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.infrastructure.config.MessageService;
import com.tumipay.transaction_orchestrator.util.TransactionTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TransactionMapper Tests")
class TransactionMapperTest {

    private TransactionMapper mapper;
    private CustomerMapper customerMapper;
    private ObjectMapper objectMapper;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        customerMapper = mock(CustomerMapper.class);
        objectMapper = new ObjectMapper();
        messageService = mock(MessageService.class);
        
        when(messageService.getMessage("success.operation.code")).thenReturn("000");
        when(messageService.getMessage("success.operation.message")).thenReturn("Successful operation");
        
        mapper = new TransactionMapper(customerMapper, objectMapper, messageService);
    }

    private Transaction buildDomainTransaction() {
        return TransactionTestData.defaultData().build().buildDomainTransaction();
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
        transaction.process();
        transaction.complete(); // This sets processedAt

        var response = mapper.toResponse(transaction);
        var data = response.getData();

        assertThat(data.getTransactionId().toString()).isEqualTo(transaction.getId());
        assertThat(data.getClientTransactionId()).isEqualTo("CLIENT-TX-001");
        assertThat(data.getCurrency()).isEqualTo("USD");
        assertThat(data.getCountry()).isEqualTo("CO");
        assertThat(data.getAmount()).isEqualTo(10000);
        assertThat(data.getProcessedAt()).isNotNull();
    }


    @Test
    @DisplayName("Given CreateTransactionRequest, when toCommand, then delegates customer mapping to CustomerMapper")
    void givenCreateRequest_whenToCommand_thenDelegatesToCustomerMapper() {
        when(customerMapper.toCommand(any())).thenReturn(
            new CreateTransactionCommand.CustomerCommand("CC", "12345678", "+57", "3001234567",
                "john@test.com", "John", null, "Doe", null)
        );

        CreateTransactionRequest request =
            TransactionTestData.defaultData().build().buildApiRequest();

        CreateTransactionCommand cmd = mapper.toCommand(request);

        assertThat(cmd.clientTransactionId()).isEqualTo("CLIENT-TX-001");
        assertThat(cmd.currency()).isEqualTo("USD");
        assertThat(cmd.countryCode()).isEqualTo("CO");
        assertThat(cmd.amount()).isEqualTo(10000L);
        assertThat(cmd.customer()).isNotNull();
    }
}
