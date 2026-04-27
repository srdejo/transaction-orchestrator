package com.tumipay.transaction_orchestrator.application.service;

import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PaymentProviderFactory {
    private final List<PaymentProviderPort> providers;

    public PaymentProviderFactory(List<PaymentProviderPort> providers) {
        this.providers = providers;
    }

    public PaymentProviderPort getProvider(Transaction transaction) {
        return providers.stream().filter(p -> p.supports(transaction)).findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_PAYMENT_METHOD,
                        "error.unsupported.payment.method"));
    }
}
