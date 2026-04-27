package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.mapper.TransactionEntityMapper;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.SpringDataTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {
    private final SpringDataTransactionRepository repository;
    private final TransactionEntityMapper mapper;
    
    public TransactionPersistenceAdapter(SpringDataTransactionRepository repository, TransactionEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);
        if (transaction.getId() != null) {
            repository.findById(UUID.fromString(transaction.getId())).ifPresent(existing -> {
                entity.getCustomer().setId(existing.getCustomer().getId());
            });
        }
        TransactionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Transaction> findById(String id) {
        return repository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }
}
