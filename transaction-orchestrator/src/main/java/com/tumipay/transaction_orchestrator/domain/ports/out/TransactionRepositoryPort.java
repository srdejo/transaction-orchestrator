package com.tumipay.transaction_orchestrator.domain.ports.out;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(String id);
}
