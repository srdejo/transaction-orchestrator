package com.tumipay.transaction_orchestrator.domain.ports.out;

import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import java.util.Optional;

public interface PaymentMethodRepositoryPort {
    Optional<PaymentMethod> findById(String id);
}
