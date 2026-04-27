package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.mapper;

import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CountryEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.PaymentMethodEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionEntityMapper {

    private final EntityManager entityManager;
    private final CustomerEntityMapper customerMapper;

    public TransactionEntityMapper(EntityManager entityManager, CustomerEntityMapper customerMapper) {
        this.entityManager = entityManager;
        this.customerMapper = customerMapper;
    }

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null)
            return null;

        CurrencyEntity currencyEntity = entityManager.getReference(CurrencyEntity.class,
                domain.getCurrency().code());
        CountryEntity countryEntity = entityManager.getReference(CountryEntity.class, domain.getCountryCode().value());
        PaymentMethodEntity paymentMethodEntity = entityManager.getReference(PaymentMethodEntity.class,
                UUID.fromString(domain.getPaymentMethod().getId()));

        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId() != null ? UUID.fromString(domain.getId()) : UUID.randomUUID());
        entity.setCustomerTransactionId(domain.getClientTransactionId());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(currencyEntity);
        entity.setCountryCode(countryEntity);
        entity.setPaymentMethodId(paymentMethodEntity);
        entity.setWebhookUrl(domain.getWebhookUrl());
        entity.setRedirectUrl(domain.getRedirectUrl());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setProcessedAt(domain.getProcessedAt());
        entity.setExpirationTime(domain.getExpirationTime());
        entity.setCustomer(customerMapper.toEntity(domain.getCustomer()));
        entity.setProviderResponse(domain.getProviderResponse());

        return entity;
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null)
            return null;

        return Transaction.reconstruct(
                entity.getId().toString(),
                entity.getCustomerTransactionId(),
                entity.getAmount(),
                new Currency(entity.getCurrency().getCurrencyCode()),
                new CountryCode(entity.getCountryCode().getCountryCode()),
                new PaymentMethod(entity.getPaymentMethodId().getId().toString()),
                entity.getWebhookUrl(),
                entity.getRedirectUrl(),
                customerMapper.toDomain(entity.getCustomer()),
                entity.getDescription(),
                entity.getExpirationTime(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getProviderResponse(),
                entity.getProcessedAt());
    }
}
