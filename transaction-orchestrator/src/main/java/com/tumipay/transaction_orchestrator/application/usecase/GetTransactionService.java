package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.GetTransactionUseCase;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import com.tumipay.transaction_orchestrator.infrastructure.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetTransactionService implements GetTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public GetTransactionService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction execute(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }
}
