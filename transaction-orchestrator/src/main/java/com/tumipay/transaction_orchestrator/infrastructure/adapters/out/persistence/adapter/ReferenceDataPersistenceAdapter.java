package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter;

import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReferenceDataPersistenceAdapter implements ReferenceDataPort {

    private final ReferenceDataCacheLoader cacheLoader;

    @Override
    public boolean isValidCountry(String countryCode) {
        if (countryCode == null) return false;
        Set<String> validCountries = cacheLoader.getValidCountries();
        return validCountries.contains(countryCode.trim().toUpperCase());
    }

    @Override
    public boolean isValidCurrency(String currencyCode) {
        if (currencyCode == null) return false;
        Set<String> validCurrencies = cacheLoader.getValidCurrencies();
        return validCurrencies.contains(currencyCode.trim().toUpperCase());
    }

    @Override
    public boolean isValidPaymentMethod(String paymentMethodId) {
        if (paymentMethodId == null) return false;
        Set<String> validPaymentMethods = cacheLoader.getValidPaymentMethods();
        return validPaymentMethods.contains(paymentMethodId);
    }

    @Override
    public boolean isCardPaymentMethod(String paymentMethodId) {
        if (paymentMethodId == null) return false;
        String cardId = cacheLoader.getCardPaymentMethodId();
        return paymentMethodId.equals(cardId);
    }
}
