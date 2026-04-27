package com.tumipay.transaction_orchestrator.infrastructure.adapters.in.rest;

import com.tumipay.transaction_orchestrator.application.mapper.TransactionMapper;
import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.GetTransactionUseCase;
import com.tumipay.transaction_orchestrator.infrastructure.config.MessageService;
import com.tumipay.transaction_orchestrator.infrastructure.exception.GlobalExceptionHandler;
import com.tumipay.transaction_orchestrator.infrastructure.exception.ValidationErrorMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({GlobalExceptionHandler.class, ValidationErrorMapper.class})
@DisplayName("Transaction Amount Validation Tests")
class TransactionValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTransactionUseCase createUseCase;
    @MockitoBean
    private GetTransactionUseCase getUseCase;
    @MockitoBean
    private TransactionMapper mapper;
    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("Given amount as string, when create, then returns 400 Bad Request due to strict type checking")
    void givenAmountAsString_whenCreate_thenReturns400() throws Exception {
        String request = """
            {
                "client_transaction_id": "TX-STRING",
                "amount": "10000",
                "currency": "USD",
                "country": "CO",
                "payment_method_id": "550e8400-e29b-41d4-a716-446655440001",
                "customer": {
                    "document_type": "CC",
                    "document_number": "12345678",
                    "email": "test@test.com",
                    "first_name": "John",
                    "last_name": "Doe"
                }
            }
            """;

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given amount with thousands separator, when create, then returns 400 Bad Request")
    void givenAmountWithThousandsSeparator_whenCreate_thenReturns400() throws Exception {
        String request = """
            {
                "client_transaction_id": "TX-SEPARATOR",
                "amount": "1,000",
                "currency": "USD",
                "country": "CO",
                "payment_method_id": "550e8400-e29b-41d4-a716-446655440001",
                "customer": {
                    "document_type": "CC",
                    "document_number": "12345678",
                    "email": "test@test.com",
                    "first_name": "John",
                    "last_name": "Doe"
                }
            }
            """;

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given amount as decimal, when create, then returns 400 Bad Request")
    void givenAmountAsDecimal_whenCreate_thenReturns400() throws Exception {
        String request = """
            {
                "client_transaction_id": "TX-DECIMAL",
                "amount": 100.50,
                "currency": "USD",
                "country": "CO",
                "payment_method_id": "550e8400-e29b-41d4-a716-446655440001",
                "customer": {
                    "document_type": "CC",
                    "document_number": "12345678",
                    "email": "test@test.com",
                    "first_name": "John",
                    "last_name": "Doe"
                }
            }
            """;

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }
}
