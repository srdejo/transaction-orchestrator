package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ValidationErrorMapper {

    public ErrorCode mapFieldToErrorCode(String fieldName) {
        if (fieldName == null) return ErrorCode.VALIDATION_ERROR;
        
        if (fieldName.contains("email")) return ErrorCode.INVALID_EMAIL;
        if (fieldName.contains("webhook_url")) return ErrorCode.INVALID_WEBHOOK_URL;
        if (fieldName.contains("return_url")) return ErrorCode.INVALID_RETURN_URL;
        if (fieldName.contains("phone")) return ErrorCode.INVALID_PHONE;
        if (fieldName.contains("amount")) return ErrorCode.INVALID_AMOUNT;
        if (fieldName.contains("document")) return ErrorCode.INVALID_DOCUMENT;
        
        return ErrorCode.VALIDATION_ERROR;
    }
}
