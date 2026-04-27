package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CountryEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CountryRepository;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CurrencyRepository;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.PaymentMethodRepository;
import com.tumipay.transaction_orchestrator.infrastructure.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReferenceDataCacheLoader {

    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Cacheable(AppConstants.CACHE_COUNTRIES)
    public Set<String> getValidCountries() {
        log.info("Loading valid countries from database to cache...");
        return countryRepository.findAll().stream()
                .map(CountryEntity::getCountryCode)
                .collect(Collectors.toSet());
    }

    @Cacheable(AppConstants.CACHE_CURRENCIES)
    public Set<String> getValidCurrencies() {
        log.info("Loading valid currencies from database to cache...");
        return currencyRepository.findAll().stream()
                .map(CurrencyEntity::getCurrencyCode)
                .collect(Collectors.toSet());
    }

    @Cacheable(AppConstants.CACHE_PAYMENT_METHODS)
    public Set<String> getValidPaymentMethods() {
        log.info("Loading valid payment methods from database to cache...");
        return paymentMethodRepository.findAll().stream()
                .map(entity -> entity.getId().toString())
                .collect(Collectors.toSet());
    }

    @Cacheable(AppConstants.CACHE_CARD_PAYMENT_METHOD_ID)
    public String getCardPaymentMethodId() {
        log.info("Loading CARD payment method ID from database to cache...");
        return paymentMethodRepository.findByCode(AppConstants.PAYMENT_METHOD_CARD)
                .map(entity -> entity.getId().toString())
                .orElse("");
    }
}
