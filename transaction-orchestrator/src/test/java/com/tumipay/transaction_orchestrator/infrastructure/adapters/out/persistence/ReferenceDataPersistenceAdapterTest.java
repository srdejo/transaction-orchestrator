package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter.ReferenceDataCacheLoader;
import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.adapter.ReferenceDataPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReferenceDataPersistenceAdapter Tests")
class ReferenceDataPersistenceAdapterTest {

    @Mock
    private ReferenceDataCacheLoader cacheLoader;

    private ReferenceDataPersistenceAdapter adapter;

    private final String pmId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        adapter = new ReferenceDataPersistenceAdapter(cacheLoader);
        lenient().when(cacheLoader.getValidCountries()).thenReturn(new java.util.HashSet<>(java.util.Arrays.asList("CO", "US", "MX")));
        lenient().when(cacheLoader.getValidCurrencies()).thenReturn(new java.util.HashSet<>(java.util.Arrays.asList("USD", "COP", "MXN")));
        lenient().when(cacheLoader.getValidPaymentMethods()).thenReturn(new java.util.HashSet<>(java.util.Arrays.asList(pmId)));
    }

    @Test
    @DisplayName("Given null country code, when isValidCountry, then returns false")
    void givenNullCountry_whenIsValidCountry_thenReturnsFalse() {
        assertThat(adapter.isValidCountry(null)).isFalse();
    }

    @Test
    @DisplayName("Given valid country code (case insensitive), when isValidCountry, then returns true")
    void givenValidCountryCode_whenIsValidCountry_thenReturnsTrue() {
        assertThat(adapter.isValidCountry("co")).isTrue();   // lowercase
        assertThat(adapter.isValidCountry("CO")).isTrue();   // uppercase
        assertThat(adapter.isValidCountry(" CO ")).isTrue(); // with spaces
    }

    @Test
    @DisplayName("Given invalid country code, when isValidCountry, then returns false")
    void givenInvalidCountry_whenIsValidCountry_thenReturnsFalse() {
        assertThat(adapter.isValidCountry("XX")).isFalse();
    }

    @Test
    @DisplayName("Given null currency, when isValidCurrency, then returns false")
    void givenNullCurrency_whenIsValidCurrency_thenReturnsFalse() {
        assertThat(adapter.isValidCurrency(null)).isFalse();
    }

    @Test
    @DisplayName("Given valid currency code, when isValidCurrency, then returns true")
    void givenValidCurrency_whenIsValidCurrency_thenReturnsTrue() {
        assertThat(adapter.isValidCurrency("USD")).isTrue();
        assertThat(adapter.isValidCurrency("usd")).isTrue();
    }

    @Test
    @DisplayName("Given invalid currency code, when isValidCurrency, then returns false")
    void givenInvalidCurrency_whenIsValidCurrency_thenReturnsFalse() {
        assertThat(adapter.isValidCurrency("GBP")).isFalse();
    }

    @Test
    @DisplayName("Given null paymentMethodId, when isValidPaymentMethod, then returns false")
    void givenNullPaymentMethod_whenIsValidPaymentMethod_thenReturnsFalse() {
        assertThat(adapter.isValidPaymentMethod(null)).isFalse();
    }

    @Test
    @DisplayName("Given valid paymentMethodId, when isValidPaymentMethod, then returns true")
    void givenValidPaymentMethodId_whenIsValidPaymentMethod_thenReturnsTrue() {
        assertThat(adapter.isValidPaymentMethod(pmId)).isTrue();
    }

    @Test
    @DisplayName("Given invalid paymentMethodId, when isValidPaymentMethod, then returns false")
    void givenInvalidPaymentMethodId_whenIsValidPaymentMethod_thenReturnsFalse() {
        assertThat(adapter.isValidPaymentMethod(UUID.randomUUID().toString())).isFalse();
    }
}
