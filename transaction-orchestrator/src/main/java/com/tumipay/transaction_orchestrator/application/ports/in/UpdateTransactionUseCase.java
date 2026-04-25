package com.tumipay.transaction_orchestrator.application.ports.in;

import com.tumipay.transaction_orchestrator.application.ports.in.command.UpdateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;

public interface UpdateTransactionUseCase {
    Transaction execute(String id, UpdateTransactionCommand command);
}
