package com.tumipay.transaction_orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tumipay.transaction_orchestrator.application.service.PaymentProviderFactory;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DisplayName("Transaction Orchestrator E2E Tests")
class TransactionOrchestratorE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepositoryPort transactionRepository;

    // Mock the external payment provider to avoid real HTTP calls & control test outcomes
    @MockitoBean
    private PaymentProviderFactory paymentProviderFactory;

    private PaymentProviderPort paymentProviderPort;

    private ObjectMapper objectMapper;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        paymentProviderPort = mock(PaymentProviderPort.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        baseUrl = "http://localhost:" + port + "/api/v1";
    }

    private HttpHeaders jsonHeaders(String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (idempotencyKey != null) {
            headers.set("X-Idempotency-Key", idempotencyKey);
        }
        return headers;
    }

    private String buildCreateRequest(String clientTxId) {
        return """
            {
                "client_transaction_id": "%s",
                "amount": 10000,
                "currency": "USD",
                "country": "CO",
                "payment_method_id": "550e8400-e29b-41d4-a716-446655440001",
                "webhook_url": "https://webhook.example.com/notify",
                "redirect_url": "https://app.example.com/return",
                "customer": {
                    "document_type": "CC",
                    "document_number": "12345678",
                    "country_calling_code": "+57",
                    "phone_number": "3001234567",
                    "email": "john.doe@example.com",
                    "first_name": "John",
                    "last_name": "Doe"
                },
                "description": "E2E Test payment"
            }
            """.formatted(clientTxId);
    }

    @Nested
    @DisplayName("Successful Transaction Lifecycle")
    class SuccessfulLifecycle {

        @Test
        @DisplayName("POST /transactions — Returns 201 with PENDING status immediately")
        void givenValidRequest_whenCreate_thenReturns201WithPendingStatus() {
            // Arrange: factory returns our mock provider
            when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
            when(paymentProviderPort.supports(any())).thenReturn(true);
            when(paymentProviderPort.processPayment(any())).thenAnswer(inv -> {
                Transaction tx = inv.getArgument(0);
                tx.complete();
                return tx;
            });

            String clientTxId = "E2E-TX-" + UUID.randomUUID();
            HttpEntity<String> request = new HttpEntity<>(buildCreateRequest(clientTxId), jsonHeaders(null));

            // Act
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, request, Map.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                System.err.println("E2E ERROR BODY: " + response.getBody());
            }

            // Assert — immediate PENDING response
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("code")).isEqualTo("000");

            Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
            assertThat(data).isNotNull();
            assertThat(data.get("status")).isEqualTo("PENDING");
            String txId = data.get("transaction_id").toString();
            assertThat(txId).isNotBlank();

            // Assert — async processing completes the transaction in the DB
            await().atMost(5, TimeUnit.SECONDS).pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    var tx = transactionRepository.findById(txId);
                    assertThat(tx).isPresent();
                    assertThat(tx.get().getStatus()).isEqualTo(TransactionStatus.SUCCESS);
                });
        }

        @Test
        @DisplayName("GET /transactions/{id} — Returns 200 with transaction data after creation")
        void givenCreatedTransaction_whenGet_thenReturns200WithData() {
            // Arrange: create a transaction first
            when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
            when(paymentProviderPort.supports(any())).thenReturn(true);
            when(paymentProviderPort.processPayment(any())).thenAnswer(inv -> {
                Transaction tx = inv.getArgument(0);
                tx.complete();
                return tx;
            });

            String clientTxId = "E2E-GET-" + UUID.randomUUID();
            HttpEntity<String> createRequest = new HttpEntity<>(buildCreateRequest(clientTxId), jsonHeaders(null));
            ResponseEntity<Map> createResponse = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, createRequest, Map.class
            );
            assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            Map<?, ?> data = (Map<?, ?>) createResponse.getBody().get("data");
            String txId = data.get("transaction_id").toString();

            // Act
            ResponseEntity<Map> getResponse = restTemplate.exchange(
                baseUrl + "/transactions/" + txId, HttpMethod.GET,
                new HttpEntity<>(jsonHeaders(null)), Map.class
            );

            // Assert
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            Map<?, ?> getData = (Map<?, ?>) getResponse.getBody().get("data");
            assertThat(getData.get("transaction_id").toString()).isEqualTo(txId);
            assertThat(getData.get("client_transaction_id")).isEqualTo(clientTxId);
        }
    }

    @Nested
    @DisplayName("Idempotency Flow")
    class IdempotencyFlow {

        @Test
        @DisplayName("Given same clientTransactionId, second request returns 200 with cached response")
        void givenSameClientTransactionId_secondRequestReturnsCachedResponse() {
            when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
            when(paymentProviderPort.supports(any())).thenReturn(true);
            when(paymentProviderPort.processPayment(any())).thenAnswer(inv -> {
                Transaction tx = inv.getArgument(0);
                tx.complete();
                return tx;
            });

            String clientTxId = "E2E-IDEM-" + UUID.randomUUID();
            String requestBody = buildCreateRequest(clientTxId);
            HttpEntity<String> request = new HttpEntity<>(requestBody, jsonHeaders(null));

            // First request — should succeed with 201
            ResponseEntity<Map> firstResponse = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, request, Map.class
            );
            assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            // Wait briefly for the response to be cached in Redis
            await().atMost(3, TimeUnit.SECONDS).pollInterval(Duration.ofMillis(300))
                .until(() -> {
                    ResponseEntity<Map> r = restTemplate.exchange(
                        baseUrl + "/transactions", HttpMethod.POST, request, Map.class
                    );
                    return r.getStatusCode() == HttpStatus.OK;
                });

            // Second request — should return 200 with cached response (not 201)
            ResponseEntity<Map> secondResponse = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, request, Map.class
            );
            assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(secondResponse.getBody().get("code")).isEqualTo("000");
        }
    }

    @Nested
    @DisplayName("Error Flows")
    class ErrorFlows {

        @Test
        @DisplayName("GET non-existing transaction — Returns 404 with code 003")
        void givenNonExistingId_whenGet_thenReturns404() {
            String nonExistingId = UUID.randomUUID().toString();

            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/transactions/" + nonExistingId, HttpMethod.GET,
                new HttpEntity<>(jsonHeaders(null)), Map.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().get("code")).isEqualTo("003");
        }

        @Test
        @DisplayName("POST with invalid country code — Returns 400 with code 001")
        void givenInvalidCountry_whenCreate_thenReturns400() {
            String body = """
                {
                    "client_transaction_id": "E2E-INVALID-COUNTRY",
                    "amount": 10000,
                    "currency": "USD",
                    "country": "INVALID_COUNTRY_XX",
                    "payment_method_id": "550e8400-e29b-41d4-a716-446655440001",
                    "webhook_url": "https://webhook.example.com/notify",
                    "redirect_url": "https://app.example.com/return",
                    "customer": {
                        "document_type": "CC",
                        "document_number": "12345678",
                        "country_calling_code": "+57",
                        "phone_number": "3001234567",
                        "email": "john.doe@example.com",
                        "first_name": "John",
                        "last_name": "Doe"
                    }
                }
                """;

            HttpEntity<String> request = new HttpEntity<>(body, jsonHeaders(null));
            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, request, Map.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().get("code")).isEqualTo("001");
        }

        @Test
        @DisplayName("POST with failing provider — Transaction ends in FAILED state")
        void givenFailingProvider_whenCreate_thenTransactionIsFailed() {
            when(paymentProviderFactory.getProvider(any())).thenReturn(paymentProviderPort);
            when(paymentProviderPort.supports(any())).thenReturn(true);
            when(paymentProviderPort.processPayment(any())).thenThrow(
                new RuntimeException("Provider unavailable")
            );

            String clientTxId = "E2E-FAIL-" + UUID.randomUUID();
            HttpEntity<String> request = new HttpEntity<>(buildCreateRequest(clientTxId), jsonHeaders(null));

            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/transactions", HttpMethod.POST, request, Map.class
            );

            // Should still return 201 PENDING immediately
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
            String txId = data.get("transaction_id").toString();

            // Async processing fails → transaction should end up FAILED
            await().atMost(5, TimeUnit.SECONDS).pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    var tx = transactionRepository.findById(txId);
                    assertThat(tx).isPresent();
                    assertThat(tx.get().getStatus()).isEqualTo(TransactionStatus.FAILED);
                });
        }
    }
}
