package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
