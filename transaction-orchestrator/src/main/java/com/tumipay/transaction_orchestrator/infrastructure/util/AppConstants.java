package com.tumipay.transaction_orchestrator.infrastructure.util;

public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // Cache Names
    public static final String CACHE_COUNTRIES = "valid_countries";
    public static final String CACHE_CURRENCIES = "valid_currencies";
    public static final String CACHE_PAYMENT_METHODS = "valid_payment_methods";
    public static final String CACHE_CARD_PAYMENT_METHOD_ID = "card_payment_method_id";

    // Database Constraints
    public static final String CONSTRAINT_DUPLICATE_TRANSACTION = "ux_transactions_customer_transaction_id";

    // Payment Method Codes
    public static final String PAYMENT_METHOD_CARD = "CARD";
    
    // Provider Responses (Mock)
    public static final String MOCK_SUCCESS_RESPONSE = "{\"status\": \"approved\", \"transaction_id\": \"PROV-SUCCESS-123\", \"message\": \"Payment successful\"}";
    public static final String MOCK_FAIL_RESPONSE = "{\"error\": \"insufficient_funds\", \"error_code\": \"105\", \"message\": \"The customer has insufficient funds\"}";
}
