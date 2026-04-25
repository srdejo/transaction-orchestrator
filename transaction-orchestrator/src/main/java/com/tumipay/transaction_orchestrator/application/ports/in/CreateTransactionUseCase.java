package com.tumipay.transaction_orchestrator.application.ports.in;

import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;

public interface CreateTransactionUseCase {
    Transaction execute(CreateTransactionCommand command);
}
