package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;

public class ProviderException extends BusinessException {
    public ProviderException(String message) {
        super(ErrorCode.PROVIDER_ERROR, message);
    }
}
