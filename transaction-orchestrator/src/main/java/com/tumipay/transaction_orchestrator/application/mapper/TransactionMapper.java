package com.tumipay.transaction_orchestrator.application.mapper;

import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.api.model.TransactionData;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import com.tumipay.transaction_orchestrator.api.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tumipay.transaction_orchestrator.infrastructure.config.MessageService;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
public class TransactionMapper {

    private final CustomerMapper customerMapper;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public TransactionMapper(CustomerMapper customerMapper, ObjectMapper objectMapper, MessageService messageService) {
        this.customerMapper = customerMapper;
        this.objectMapper = objectMapper;
        this.messageService = messageService;
    }

    public CreateTransactionCommand toCommand(CreateTransactionRequest request) {
        return new CreateTransactionCommand(
            request.getClientTransactionId(),
            BigDecimal.valueOf(request.getAmount()),
            request.getCurrency(),
            request.getCountry(),
            request.getPaymentMethodId() != null ? request.getPaymentMethodId().toString() : null,
            request.getWebhookUrl() != null ? request.getWebhookUrl() : null,
            request.getReturnUrl() != null ? request.getReturnUrl() : null,
            customerMapper.toCommand(request.getCustomer()),
            request.getDescription(),
            request.getExpirationTime() != null ? LocalDateTime.now().plusSeconds(request.getExpirationTime()) : null
        );
    }


    public TransactionResponseWrapper toResponse(Transaction transaction) {
        TransactionData data = new TransactionData()
            .transactionId(UUID.fromString(transaction.getId()))
            .clientTransactionId(transaction.getClientTransactionId())
            .paymentMethodId(transaction.getPaymentMethod().getId())
            .amount(transaction.getAmount().amount().intValue())
            .currency(transaction.getAmount().currency().code())
            .country(transaction.getCountryCode().value())
            .description(transaction.getDescription())
            .status(TransactionStatus.fromValue(transaction.getStatus().name()))
            .createdAt(transaction.getCreatedAt().atOffset(ZoneOffset.UTC));

        if (transaction.getProviderResponse() != null) {
            try {
                data.providerResponse(objectMapper.readValue(transaction.getProviderResponse(), Object.class));
            } catch (JsonProcessingException e) {
                log.error("Error parsing provider response for transaction {}: {}", transaction.getId(), e.getMessage());
            }
        }

        TransactionResponseWrapper response = new TransactionResponseWrapper();
        response.setCode(messageService.getMessage("success.operation.code"));
        response.setMessage(messageService.getMessage("success.operation.message"));
        response.setData(data);
        return response;
    }
}
