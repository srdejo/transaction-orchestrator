package com.tumipay.transaction_orchestrator.domain.model;

import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private final String clientTransactionId;
    private final long amount;
    private final Currency currency;
    private final CountryCode countryCode;
    private final PaymentMethod paymentMethod;
    private final String webhookUrl;
    private final String redirectUrl;
    private final Customer customer;
    private final String description;
    private final LocalDateTime expirationTime;
    
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String providerResponse;

    public Transaction(String clientTransactionId, long amount, Currency currency, CountryCode countryCode, 
                       PaymentMethod paymentMethod, String webhookUrl, String redirectUrl, 
                       Customer customer, String description, LocalDateTime expirationTime) {
        if (clientTransactionId == null || clientTransactionId.isBlank()) throw new IllegalArgumentException("ClientTransactionId is required");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
        if (currency == null) throw new IllegalArgumentException("Currency is required");
        if (countryCode == null) throw new IllegalArgumentException("CountryCode is required");
        if (paymentMethod == null) throw new IllegalArgumentException("PaymentMethod is required");
        if (webhookUrl == null || webhookUrl.isBlank()) throw new IllegalArgumentException("WebhookUrl is required");
        if (redirectUrl == null || redirectUrl.isBlank()) throw new IllegalArgumentException("RedirectUrl is required");
        if (customer == null) throw new IllegalArgumentException("Customer is required");

        this.clientTransactionId = clientTransactionId;
        this.amount = amount;
        this.currency = currency;
        this.countryCode = countryCode;
        this.paymentMethod = paymentMethod;
        this.webhookUrl = webhookUrl;
        this.redirectUrl = redirectUrl;
        this.customer = customer;
        this.description = description;
        this.expirationTime = expirationTime;
        
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public static Transaction reconstruct(String id, String clientTransactionId, long amount, Currency currency, CountryCode countryCode, 
                                           PaymentMethod paymentMethod, String webhookUrl, String redirectUrl, 
                                           Customer customer, String description, LocalDateTime expirationTime,
                                           TransactionStatus status, LocalDateTime createdAt) {
        return reconstruct(id, clientTransactionId, amount, currency, countryCode, paymentMethod, webhookUrl, redirectUrl, customer, description, expirationTime, status, createdAt, null, null);
    }

    public static Transaction reconstruct(String id, String clientTransactionId, long amount, Currency currency, CountryCode countryCode, 
                                           PaymentMethod paymentMethod, String webhookUrl, String redirectUrl, 
                                           Customer customer, String description, LocalDateTime expirationTime,
                                           TransactionStatus status, LocalDateTime createdAt, String providerResponse) {
        return reconstruct(id, clientTransactionId, amount, currency, countryCode, paymentMethod, webhookUrl, redirectUrl, customer, description, expirationTime, status, createdAt, providerResponse, null);
    }

    public static Transaction reconstruct(String id, String clientTransactionId, long amount, Currency currency, CountryCode countryCode, 
                                           PaymentMethod paymentMethod, String webhookUrl, String redirectUrl, 
                                           Customer customer, String description, LocalDateTime expirationTime,
                                           TransactionStatus status, LocalDateTime createdAt, String providerResponse, LocalDateTime processedAt) {
        Transaction t = new Transaction(clientTransactionId, amount, currency, countryCode, paymentMethod, webhookUrl, redirectUrl, customer, description, expirationTime);
        t.id = id;
        t.status = status;
        t.createdAt = createdAt;
        t.providerResponse = providerResponse;
        t.processedAt = processedAt;
        return t;
    }

    public void assignId(String id) {
        if (this.id != null) {
            throw new IllegalStateException("Transaction ID is already assigned");
        }
        this.id = id;
    }

    public void process() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transactions can be processed");
        }
        this.status = TransactionStatus.PROCESSING;
    }

    public void complete() {
        if (this.status != TransactionStatus.PROCESSING) {
            throw new IllegalStateException("Only PROCESSING transactions can be completed");
        }
        this.status = TransactionStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        if (this.status == TransactionStatus.SUCCESS) {
            throw new IllegalStateException("SUCCESS transactions cannot be cancelled");
        }
        this.status = TransactionStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }

    public void updateProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getId() { return id; }
    public String getClientTransactionId() { return clientTransactionId; }
    public long getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public CountryCode getCountryCode() { return countryCode; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getWebhookUrl() { return webhookUrl; }
    public String getRedirectUrl() { return redirectUrl; }
    public Customer getCustomer() { return customer; }
    public String getDescription() { return description; }
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getProviderResponse() { return providerResponse; }
}
