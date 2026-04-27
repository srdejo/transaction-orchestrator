package com.tumipay.transaction_orchestrator.domain.exception;

public enum ErrorCode {
    INVALID_COUNTRY("001", "error.001"),
    INVALID_CURRENCY("002", "error.002"),
    TRANSACTION_NOT_FOUND("003", "error.003"),
    INVALID_PAYMENT_METHOD("004", "error.004"),
    PROVIDER_ERROR("005", "error.005"),
    INVALID_TRANSACTION_STATE("006", "error.006"),
    VALIDATION_ERROR("400", "error.400"),
    INVALID_EMAIL("401", "error.401"),
    INVALID_WEBHOOK_URL("402", "error.402"),
    INVALID_RETURN_URL("403", "error.403"),
    INVALID_PHONE("404", "error.404"),
    INVALID_AMOUNT("405", "error.405"),
    INVALID_DOCUMENT("406", "error.406"),
    DUPLICATE_TRANSACTION("409", "error.409"),
    INTERNAL_ERROR("500", "error.500");

    private final String code;
    private final String messageKey;

    ErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
