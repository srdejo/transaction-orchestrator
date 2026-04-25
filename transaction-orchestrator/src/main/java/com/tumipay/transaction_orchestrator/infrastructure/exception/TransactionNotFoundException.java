package com.tumipay.transaction_orchestrator.infrastructure.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
