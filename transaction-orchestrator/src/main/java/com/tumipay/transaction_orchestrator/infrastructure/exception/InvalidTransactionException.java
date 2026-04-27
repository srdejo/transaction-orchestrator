package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;

public class InvalidTransactionException extends BusinessException {
    public InvalidTransactionException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
