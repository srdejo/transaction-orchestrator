package com.tumipay.transaction_orchestrator.application.ports.in;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;

public interface GetTransactionUseCase {
    Transaction execute(String id);
}
