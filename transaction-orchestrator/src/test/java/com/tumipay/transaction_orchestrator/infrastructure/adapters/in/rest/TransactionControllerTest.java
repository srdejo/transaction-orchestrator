package com.tumipay.transaction_orchestrator.infrastructure.adapters.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tumipay.transaction_orchestrator.api.model.TransactionData;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import com.tumipay.transaction_orchestrator.application.mapper.TransactionMapper;
import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.GetTransactionUseCase;
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
import com.tumipay.transaction_orchestrator.infrastructure.exception.GlobalExceptionHandler;
import com.tumipay.transaction_orchestrator.infrastructure.exception.TransactionNotFoundException;
import com.tumipay.transaction_orchestrator.infrastructure.exception.ValidationErrorMapper;
import com.tumipay.transaction_orchestrator.infrastructure.config.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({GlobalExceptionHandler.class, ValidationErrorMapper.class})
@DisplayName("TransactionController Integration Tests (WebMvcTest)")
class TransactionControllerTest {

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

    private ObjectMapper objectMapper;
    private Transaction sampleTransaction;
    private String sampleTxId;
    private TransactionResponseWrapper sampleResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new org.openapitools.jackson.nullable.JsonNullableModule());

        sampleTxId = UUID.randomUUID().toString();
        Customer customer = new Customer(
            DocumentType.CC, "12345678", "+57", "3001234567",
            "john.doe@example.com", "John", null, "Doe", null
        );
        sampleTransaction = Transaction.reconstruct(
            sampleTxId, "CLIENT-TX-001",
            new Money(BigDecimal.valueOf(10000), new Currency("USD")),
            new CountryCode("CO"),
            new PaymentMethod(UUID.randomUUID().toString()),
            "https://webhook.example.com", "https://redirect.example.com",
            customer, "Test payment", null,
            TransactionStatus.PENDING, LocalDateTime.now()
        );

        TransactionData data =
            new TransactionData()
                .transactionId(UUID.fromString(sampleTxId))
                .clientTransactionId("CLIENT-TX-001")
                .amount(10000)
                .currency("USD")
                .country("CO")
                .status(com.tumipay.transaction_orchestrator.api.model.TransactionStatus.PENDING)
                .createdAt(java.time.OffsetDateTime.now());

        sampleResponse = new TransactionResponseWrapper();
        sampleResponse.setCode("000");
        sampleResponse.setMessage("Successful operation");
        sampleResponse.setData(data);

        // Configure default message resolution for tests
        when(messageService.getMessage(any(), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (key.equals("error.validation.prefix")) return "Validation error: " + invocation.getArgument(1);
            if (key.equals("error.001.detail")) return "Invalid country code: " + invocation.getArgument(1);
            if (key.equals("error.002.detail")) return "Invalid currency code: " + invocation.getArgument(1);
            if (key.equals("error.004.detail")) return "Invalid payment method with id: " + invocation.getArgument(1);
            return key;
        });
        
        when(messageService.getMessage(any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            if (key.equals("error.validation.malformed.json")) return "Malformed JSON request or invalid data types";
            if (key.equals("error.db.duplicate.transaction")) return "A transaction with this client ID already exists";
            if (key.equals("error.db.constraint.violation")) return "Database constraint violation";
            if (key.equals("error.db.unexpected")) return "An unexpected error occurred";
            if (key.equals("error.003")) return "Transaction not found";
            return key;
        });
    }

    private String buildValidCreateRequest(String clientTxId) {
        return """
            {
                "client_transaction_id": "%s",
                "amount": 10000,
                "currency": "USD",
                "country": "CO",
                "payment_method_id": "%s",
                "webhook_url": "https://webhook.example.com/notify",
                "return_url": "https://app.example.com/return",
                "customer": {
                    "document_type": "CC",
                    "document_number": "12345678",
                    "phone_code": "+57",
                    "phone_number": "3001234567",
                    "email": "john.doe@example.com",
                    "first_name": "John",
                    "last_name": "Doe"
                },
                "description": "Test payment"
            }
            """.formatted(clientTxId, UUID.randomUUID());
    }

    @Nested
    @DisplayName("POST /transactions — Create Transaction")
    class CreateTransaction {

        @Test
        @DisplayName("Given valid request, when create, then returns 201 with PENDING status")
        void givenValidRequest_whenCreate_thenReturns201() throws Exception {
            when(mapper.toCommand(any(com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest.class))).thenReturn(null);
            when(createUseCase.execute(any())).thenReturn(sampleTransaction);
            when(mapper.toResponse(sampleTransaction)).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildValidCreateRequest("CLIENT-TX-001")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.message").value("Successful operation"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
        }

        @Test
        @DisplayName("Given invalid country, when create, then returns 400 with INVALID_COUNTRY error code")
        void givenInvalidCountry_whenCreate_thenReturns400WithInvalidCountryCode() throws Exception {
            when(mapper.toCommand(any(com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest.class))).thenReturn(null);
            when(createUseCase.execute(any()))
                .thenThrow(new BusinessException(ErrorCode.INVALID_COUNTRY, "error.001.detail", "XX"));

            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildValidCreateRequest("CLIENT-TX-002")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("001"))
                .andExpect(jsonPath("$.message").value(containsString("Invalid country code")));
        }

        @Test
        @DisplayName("Given invalid currency, when create, then returns 400 with INVALID_CURRENCY error code")
        void givenInvalidCurrency_whenCreate_thenReturns400WithInvalidCurrencyCode() throws Exception {
            when(mapper.toCommand(any(com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest.class))).thenReturn(null);
            when(createUseCase.execute(any()))
                .thenThrow(new BusinessException(ErrorCode.INVALID_CURRENCY, "error.002.detail", "XXX"));

            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildValidCreateRequest("CLIENT-TX-003")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("002"));
        }

        @Test
        @DisplayName("Given duplicate clientTransactionId (DB violation), when create, then returns 409 with DUPLICATE_TRANSACTION code")
        void givenDuplicateClientTransactionId_whenCreate_thenReturns409() throws Exception {
            when(mapper.toCommand(any(com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest.class))).thenReturn(null);
            when(createUseCase.execute(any()))
                .thenThrow(new DataIntegrityViolationException("ux_transactions_customer_transaction_id violation"));

            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildValidCreateRequest("CLIENT-TX-DUPLICATE")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
        }

        @Test
        @DisplayName("Given invalid payment method, when create, then returns 400 with INVALID_PAYMENT_METHOD code")
        void givenInvalidPaymentMethod_whenCreate_thenReturns400WithCode004() throws Exception {
            when(mapper.toCommand(any(com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest.class))).thenReturn(null);
            when(createUseCase.execute(any()))
                .thenThrow(new BusinessException(ErrorCode.INVALID_PAYMENT_METHOD, "error.004.detail", "PM-ID"));

            mockMvc.perform(post("/api/v1/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildValidCreateRequest("CLIENT-TX-004")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("004"));
        }
    }

    @Nested
    @DisplayName("GET /transactions/{id} — Get Transaction")
    class GetTransaction {

        @Test
        @DisplayName("Given existing transaction ID, when GET, then returns 200 with transaction data")
        void givenExistingId_whenGet_thenReturns200WithTransactionData() throws Exception {
            when(getUseCase.execute(sampleTxId)).thenReturn(sampleTransaction);
            when(mapper.toResponse(sampleTransaction)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/v1/transactions/{id}", sampleTxId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.data.transaction_id").value(sampleTxId));
        }

        @Test
        @DisplayName("Given non-existing transaction ID, when GET, then returns 404 with code 003")
        void givenNonExistingId_whenGet_thenReturns404WithCode003() throws Exception {
            String nonExistingId = UUID.randomUUID().toString();
            when(getUseCase.execute(nonExistingId))
                .thenThrow(new TransactionNotFoundException("error.003"));

            mockMvc.perform(get("/api/v1/transactions/{id}", nonExistingId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("003"))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }
    }

}
