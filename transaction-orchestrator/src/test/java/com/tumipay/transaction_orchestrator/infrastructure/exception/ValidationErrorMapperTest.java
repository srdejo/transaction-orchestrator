package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ValidationErrorMapper Tests")
class ValidationErrorMapperTest {

    private ValidationErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ValidationErrorMapper();
    }

    @Test
    @DisplayName("Given null field name, when map, then returns VALIDATION_ERROR")
    void givenNullField_whenMap_thenReturnsValidationError() {
        ErrorCode result = mapper.mapFieldToErrorCode(null);
        assertThat(result).isEqualTo(ErrorCode.VALIDATION_ERROR);
    }

    @ParameterizedTest(name = "Field ''{0}'' should map to {1}")
    @CsvSource({
        "customer.email, INVALID_EMAIL",
        "email, INVALID_EMAIL",
        "webhook_url, INVALID_WEBHOOK_URL",
        "return_url, INVALID_RETURN_URL",
        "customer.phone, INVALID_PHONE",
        "phone, INVALID_PHONE",
        "amount, INVALID_AMOUNT",
        "documentNumber, INVALID_DOCUMENT",
        "customer.documentType, INVALID_DOCUMENT"
    })
    @DisplayName("Given specific field name, when map, then returns correct error code")
    void givenSpecificField_whenMap_thenReturnsCorrectErrorCode(String fieldName, String expectedCode) {
        ErrorCode result = mapper.mapFieldToErrorCode(fieldName);
        assertThat(result.name()).isEqualTo(expectedCode);
    }

    @Test
    @DisplayName("Given unknown field name, when map, then returns VALIDATION_ERROR")
    void givenUnknownField_whenMap_thenReturnsValidationError() {
        ErrorCode result = mapper.mapFieldToErrorCode("someRandomUnknownField");
        assertThat(result).isEqualTo(ErrorCode.VALIDATION_ERROR);
    }
}
