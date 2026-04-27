package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.UpdateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.command.UpdateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import com.tumipay.transaction_orchestrator.infrastructure.exception.TransactionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateTransactionService implements UpdateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public UpdateTransactionService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction execute(String id, UpdateTransactionCommand command) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));

        if ("SUCCESS".equalsIgnoreCase(command.status())) {
            transaction.complete();
        } else if ("FAILED".equalsIgnoreCase(command.status())) {
            transaction.fail();
        } else if ("CANCELLED".equalsIgnoreCase(command.status())) {
            transaction.cancel();
        }

        return transactionRepository.save(transaction);
    }
}
