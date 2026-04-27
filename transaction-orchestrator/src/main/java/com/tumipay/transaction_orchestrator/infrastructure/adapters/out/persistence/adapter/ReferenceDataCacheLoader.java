package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CountryEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CountryRepository;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CurrencyRepository;
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
    private final com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.PaymentMethodRepository paymentMethodRepository;

    @Cacheable("valid_countries")
    public Set<String> getValidCountries() {
        log.info("Loading valid countries from database to cache...");
        return countryRepository.findAll().stream()
                .map(CountryEntity::getCountryCode)
                .collect(Collectors.toSet());
    }

    @Cacheable("valid_currencies")
    public Set<String> getValidCurrencies() {
        log.info("Loading valid currencies from database to cache...");
        return currencyRepository.findAll().stream()
                .map(CurrencyEntity::getCurrencyCode)
                .collect(Collectors.toSet());
    }

    @Cacheable("valid_payment_methods")
    public Set<String> getValidPaymentMethods() {
        log.info("Loading valid payment methods from database to cache...");
        return paymentMethodRepository.findAll().stream()
                .map(entity -> entity.getId().toString())
                .collect(Collectors.toSet());
    }
}
