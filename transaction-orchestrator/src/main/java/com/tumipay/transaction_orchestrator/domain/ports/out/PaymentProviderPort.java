package com.tumipay.transaction_orchestrator.domain.ports.out;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;

public interface PaymentProviderPort {
    boolean supports(Transaction transaction);
    Transaction processPayment(Transaction transaction);
}
