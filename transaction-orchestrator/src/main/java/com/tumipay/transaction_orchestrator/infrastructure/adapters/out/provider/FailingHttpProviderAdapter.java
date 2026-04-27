package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FailingHttpProviderAdapter implements PaymentProviderPort {

    private static final Logger log = LoggerFactory.getLogger(FailingHttpProviderAdapter.class);

    @Override
    public boolean supports(Transaction transaction) {
        // Si el monto termina en 99 (ej. $99.99 = 9999 cents), simulamos fallo (Fondos Insuficientes)
        return transaction.getAmount().amount().longValue() % 100 == 99;
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

        transaction.fail(); // Falla la transacción (cambia a FAILED)
        return transaction;
    }
}
