package com.tumipay.transaction_orchestrator.infrastructure.exception;

public class ProviderException extends RuntimeException {
    public ProviderException(String message) {
        super(message);
    }
}
