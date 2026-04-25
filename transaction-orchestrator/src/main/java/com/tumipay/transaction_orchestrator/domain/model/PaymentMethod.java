package com.tumipay.transaction_orchestrator.domain.model;

public class PaymentMethod {
    private final String id;

    public PaymentMethod(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("PaymentMethod ID is required");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
