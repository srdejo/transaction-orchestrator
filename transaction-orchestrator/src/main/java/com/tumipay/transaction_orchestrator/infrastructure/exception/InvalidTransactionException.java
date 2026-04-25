package com.tumipay.transaction_orchestrator.infrastructure.exception;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
