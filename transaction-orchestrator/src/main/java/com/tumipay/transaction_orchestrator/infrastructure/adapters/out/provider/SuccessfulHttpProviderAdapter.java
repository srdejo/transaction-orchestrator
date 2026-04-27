package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.provider;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SuccessfulHttpProviderAdapter implements PaymentProviderPort {

    private static final Logger log = LoggerFactory.getLogger(SuccessfulHttpProviderAdapter.class);

    @Override
    public boolean supports(Transaction transaction) {
        // En este simulador, si el monto NO termina en 99 (ej. no es 9999), se enruta al proveedor exitoso.
        return transaction.getAmount().amount().longValue() % 100 != 99;
    }

    @Override
    public Transaction processPayment(Transaction transaction) {
        log.info("=====================================================");
        log.info("[PROVEEDOR EXITOSO] Iniciando llamada REST asíncrona...");
        
        try {
            Thread.sleep(2000); // Latencia de 2 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[PROVEEDOR EXITOSO] Response HTTP 200 OK - Pago Aprobado");
        log.info("=====================================================");

        transaction.updateProviderResponse("{\"status\": \"approved\", \"transaction_id\": \"PROV-SUCCESS-123\", \"message\": \"Payment successful\"}");
        transaction.complete(); // Completa la transacción (cambia a COMPLETED)
        return transaction;
    }
}
