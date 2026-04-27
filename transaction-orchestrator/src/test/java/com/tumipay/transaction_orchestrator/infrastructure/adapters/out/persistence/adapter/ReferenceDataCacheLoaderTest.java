package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.PaymentMethodEntity;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CountryRepository;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.CurrencyRepository;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReferenceDataCacheLoader Tests")
class ReferenceDataCacheLoaderTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    private ReferenceDataCacheLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ReferenceDataCacheLoader(countryRepository, currencyRepository, paymentMethodRepository);
    }

    @Test
    @DisplayName("Given CARD exists, when getCardPaymentMethodId, then returns ID")
    void givenCardExists_whenGetCardPaymentMethodId_thenReturnsId() {
        UUID id = UUID.randomUUID();
        PaymentMethodEntity entity = new PaymentMethodEntity();
        entity.setId(id);
        entity.setCode("CARD");

        when(paymentMethodRepository.findByCode("CARD")).thenReturn(Optional.of(entity));

        String result = loader.getCardPaymentMethodId();

        assertThat(result).isEqualTo(id.toString());
    }

    @Test
    @DisplayName("Given CARD does not exist, when getCardPaymentMethodId, then returns empty string")
    void givenCardDoesNotExist_whenGetCardPaymentMethodId_thenReturnsEmptyString() {
        when(paymentMethodRepository.findByCode("CARD")).thenReturn(Optional.empty());

        String result = loader.getCardPaymentMethodId();

        assertThat(result).isEmpty();
    }
}
