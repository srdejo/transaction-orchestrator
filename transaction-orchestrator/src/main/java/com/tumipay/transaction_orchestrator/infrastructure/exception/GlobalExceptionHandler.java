package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.tumipay.transaction_orchestrator.api.model.ErrorResponseWrapper;
import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseWrapper> handleBusinessException(BusinessException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ex.getErrorCode().getCode());
        errorResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseWrapper> handleNotFound(TransactionNotFoundException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ErrorCode.TRANSACTION_NOT_FOUND.getCode());
        errorResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponseWrapper> handleInvalid(InvalidTransactionException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        errorResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<ErrorResponseWrapper> handleProvider(ProviderException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ErrorCode.PROVIDER_ERROR.getCode());
        errorResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseWrapper> handleGenericException(Exception ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ErrorCode.INTERNAL_ERROR.getCode());
        errorResponse.setMessage("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
