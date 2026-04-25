package com.tumipay.transaction_orchestrator.application.mapper;

import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.api.model.TransactionData;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import com.tumipay.transaction_orchestrator.api.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.api.model.UpdateTransactionRequest;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.application.ports.in.command.UpdateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class TransactionMapper {

    private final CustomerMapper customerMapper;

    public TransactionMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public CreateTransactionCommand toCommand(CreateTransactionRequest request) {
        return new CreateTransactionCommand(
            request.getClientTransactionId(),
            BigDecimal.valueOf(request.getAmount()),
            request.getCurrency(),
            request.getCountry(),
            request.getPaymentMethodId(),
            request.getWebhookUrl() != null ? request.getWebhookUrl().toString() : null,
            request.getReturnUrl() != null ? request.getReturnUrl().toString() : null,
            customerMapper.toCommand(request.getCustomer()),
            request.getDescription(),
            request.getExpirationTime() != null ? LocalDateTime.now().plusSeconds(request.getExpirationTime()) : null
        );
    }

    public UpdateTransactionCommand toCommand(UpdateTransactionRequest request) {
        return new UpdateTransactionCommand(
            request.getStatus().name(),
            request.getProviderTransactionId(),
            request.getMessage()
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

        TransactionResponseWrapper response = new TransactionResponseWrapper();
        response.setCode("000");
        response.setMessage("Successful operation");
        response.setData(data);
        return response;
    }
}
