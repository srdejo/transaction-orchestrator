package com.tumipay.transaction_orchestrator.util;

import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.TransactionStatus;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(setterPrefix = "with")
public class TransactionTestData {
    @Builder.Default
    private String clientTransactionId = "CLIENT-TX-001";
    @Builder.Default
    private Long amount = 10000L;
    @Builder.Default
    private String currency = "USD";
    @Builder.Default
    private String country = "CO";
    @Builder.Default
    private String paymentMethodId = "550e8400-e29b-41d4-a716-446655440001";
    @Builder.Default
    private String webhookUrl = "https://webhook.example.com/notify";
    @Builder.Default
    private String redirectUrl = "https://app.example.com/return";
    @Builder.Default
    private String description = "Test payment";
    
    // Customer fields
    @Builder.Default
    private String documentType = "CC";
    @Builder.Default
    private String documentNumber = "12345678";
    @Builder.Default
    private String countryCallingCode = "+57";
    @Builder.Default
    private String phoneNumber = "3001234567";
    @Builder.Default
    private String email = "john.doe@example.com";
    @Builder.Default
    private String firstName = "John";
    @Builder.Default
    private String lastName = "Doe";

    public static TransactionTestData.TransactionTestDataBuilder defaultData() {
        return TransactionTestData.builder();
    }

    public CreateTransactionRequest buildApiRequest() {
        com.tumipay.transaction_orchestrator.api.model.Customer apiCustomer = 
            new com.tumipay.transaction_orchestrator.api.model.Customer()
                .documentType(documentType)
                .documentNumber(documentNumber)
                .countryCallingCode(countryCallingCode)
                .phoneNumber(phoneNumber)
                .email(email)
                .firstName(firstName)
                .lastName(lastName);
                
        return new CreateTransactionRequest()
            .clientTransactionId(clientTransactionId)
            .amount(amount.intValue())
            .currency(currency)
            .country(country)
            .paymentMethodId(UUID.fromString(paymentMethodId))
            .webhookUrl(webhookUrl)
            .redirectUrl(redirectUrl)
            .customer(apiCustomer)
            .description(description);
    }
    
    public Transaction buildDomainTransaction() {
        return buildDomainTransaction(UUID.randomUUID().toString());
    }

    public Transaction buildDomainTransaction(String txId) {
        Customer domainCustomer = new Customer(
            DocumentType.valueOf(documentType), documentNumber, countryCallingCode, phoneNumber,
            email, firstName, null, lastName, null
        );
        
        return Transaction.reconstruct(
            txId, clientTransactionId, amount, new Currency(currency), new CountryCode(country),
            new PaymentMethod(paymentMethodId), webhookUrl, redirectUrl, domainCustomer,
            description, null, TransactionStatus.PENDING, LocalDateTime.now(), null
        );
    }

    public CreateTransactionCommand buildCommand() {
        CreateTransactionCommand.CustomerCommand customerCmd = new CreateTransactionCommand.CustomerCommand(
            documentType, documentNumber, countryCallingCode, phoneNumber,
            email, firstName, null, lastName, null
        );
        
        return new CreateTransactionCommand(
            clientTransactionId, amount, currency, country, paymentMethodId,
            webhookUrl, redirectUrl, customerCmd, description, null
        );
    }

    public java.util.Map<String, Object> buildApiRequestMap() {
        java.util.Map<String, Object> customer = new java.util.HashMap<>();
        customer.put("document_type", documentType);
        customer.put("document_number", documentNumber);
        customer.put("country_calling_code", countryCallingCode);
        customer.put("phone_number", phoneNumber);
        customer.put("email", email);
        customer.put("first_name", firstName);
        customer.put("last_name", lastName);

        java.util.Map<String, Object> request = new java.util.HashMap<>();
        request.put("client_transaction_id", clientTransactionId);
        request.put("amount", amount);
        request.put("currency", currency);
        request.put("country", country);
        request.put("payment_method_id", paymentMethodId);
        request.put("webhook_url", webhookUrl);
        request.put("redirect_url", redirectUrl);
        request.put("customer", customer);
        request.put("description", description);
        return request;
    }
}
