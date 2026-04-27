package com.tumipay.transaction_orchestrator.domain.ports.out;

public interface ReferenceDataPort {
    boolean isValidCountry(String countryCode);
    boolean isValidCurrency(String currencyCode);
    boolean isValidPaymentMethod(String paymentMethodId);
    boolean isCardPaymentMethod(String paymentMethodId);
}
