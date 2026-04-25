package com.tumipay.transaction_orchestrator.application.service;

import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.ports.out.PaymentProviderPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncPaymentProcessor {
    private static final Logger log = LoggerFactory.getLogger(AsyncPaymentProcessor.class);
    private final TransactionRepositoryPort transactionRepository;
    private final PaymentProviderFactory paymentProviderFactory;

    public AsyncPaymentProcessor(TransactionRepositoryPort transactionRepository, PaymentProviderFactory paymentProviderFactory) {
        this.transactionRepository = transactionRepository;
        this.paymentProviderFactory = paymentProviderFactory;
    }

    @Async
    public void process(Transaction transaction) {
        log.info("Procesando pago en hilo asíncrono para TxID: {}", transaction.getId());
        try {
            PaymentProviderPort provider = paymentProviderFactory.getProvider(transaction);
            
            transaction.process();
            transactionRepository.save(transaction);
            
            Transaction processedTransaction = provider.processPayment(transaction);
            transactionRepository.save(processedTransaction);
            
            // Aquí en un futuro se dispararía el Webhook con la respuesta
        } catch (Exception e) {
            log.error("Error procesando pago asincrono", e);
            transaction.fail();
            transactionRepository.save(transaction);
        }
    }
}
