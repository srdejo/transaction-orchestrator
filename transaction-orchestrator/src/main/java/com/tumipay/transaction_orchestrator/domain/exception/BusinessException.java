package com.tumipay.transaction_orchestrator.domain.exception;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String messageKey;
    private final Object[] args;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.messageKey = errorCode.getMessageKey();
        this.args = new Object[0];
    }

    public BusinessException(ErrorCode errorCode, String messageKey, Object... args) {
        super(messageKey);
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
