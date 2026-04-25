package com.tumipay.transaction_orchestrator.infrastructure.adapters.outbound.persistence.mapper;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.outbound.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerEntityMapper {

    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) return null;
        
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID()); 
        entity.setDocumentType(domain.getDocumentType().name());
        entity.setDocumentNumber(domain.getDocumentNumber());
        entity.setPhoneCode(domain.getCountryCallCode());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setMiddleName(domain.getMiddleName());
        entity.setLastName(domain.getLastName());
        entity.setSecondLastName(domain.getSecondLastName());
        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) return null;
        
        return new Customer(
            DocumentType.valueOf(entity.getDocumentType()),
            entity.getDocumentNumber(),
            entity.getPhoneCode(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getFirstName(),
            entity.getMiddleName(),
            entity.getLastName(),
            entity.getSecondLastName()
        );
    }
}
