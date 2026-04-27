package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import com.tumipay.transaction_orchestrator.infrastructure.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailingHttpProviderAdapter implements PaymentProviderPort {

    private static final Logger log = LoggerFactory.getLogger(FailingHttpProviderAdapter.class);
    private final ReferenceDataPort referenceDataPort;

    @Override
    public boolean supports(Transaction transaction) {
        return referenceDataPort.isCardPaymentMethod(transaction.getPaymentMethod().getId());
    }

    @Override
    public Transaction processPayment(Transaction transaction) {
        log.info("=====================================================");
        log.info("[PROVEEDOR FALLIDO] Iniciando llamada REST asíncrona...");
        
        try {
            Thread.sleep(1500); // Latencia
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.error("[PROVEEDOR FALLIDO] Response HTTP 400 Bad Request - Fondos Insuficientes");
        log.info("=====================================================");

        transaction.updateProviderResponse(AppConstants.MOCK_FAIL_RESPONSE);
        transaction.fail(); // Falla la transacción (cambia a FAILED)
        return transaction;
    }
}
