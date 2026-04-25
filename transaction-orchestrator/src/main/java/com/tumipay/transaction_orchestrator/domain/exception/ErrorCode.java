package com.tumipay.transaction_orchestrator.domain.exception;

public enum ErrorCode {
    INVALID_COUNTRY("001", "Invalid country code"),
    INVALID_CURRENCY("002", "Invalid currency code"),
    TRANSACTION_NOT_FOUND("003", "Transaction not found"),
    INVALID_PAYMENT_METHOD("004", "Invalid payment method"),
    PROVIDER_ERROR("005", "Payment provider error"),
    INVALID_TRANSACTION_STATE("006", "Invalid transaction state"),
    VALIDATION_ERROR("400", "Validation error"),
    INTERNAL_ERROR("500", "Internal server error");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
