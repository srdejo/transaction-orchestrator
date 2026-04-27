package com.tumipay.transaction_orchestrator.infrastructure.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tumipay.transaction_orchestrator.api.model.ErrorResponseWrapper;
import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tumipay.transaction_orchestrator.infrastructure.config.MessageService;
import com.tumipay.transaction_orchestrator.infrastructure.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ValidationErrorMapper validationErrorMapper;
    private final MessageService messageService;

    public GlobalExceptionHandler(ValidationErrorMapper validationErrorMapper, MessageService messageService) {
        this.validationErrorMapper = validationErrorMapper;
        this.messageService = messageService;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseWrapper> handleBusinessException(BusinessException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ex.getErrorCode().getCode());
        errorResponse.setMessage(messageService.getMessage(ex.getMessageKey(), ex.getArgs()));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseWrapper> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", errorMessage);

        String firstField = ex.getBindingResult().getFieldErrors().isEmpty() ? null
                : ex.getBindingResult().getFieldErrors().get(0).getField();
        ErrorCode mappedCode = validationErrorMapper.mapFieldToErrorCode(firstField);

        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(mappedCode.getCode());
        errorResponse.setMessage(messageService.getMessage("error.validation.prefix", errorMessage));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseWrapper> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = messageService.getMessage("error.validation.malformed.json");
        ErrorCode mappedCode = ErrorCode.VALIDATION_ERROR;

        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getPath() != null && !invalidFormatException.getPath().isEmpty()) {
                String path = invalidFormatException.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining("."));
                message = messageService.getMessage("error.validation.invalid.type", path);
                mappedCode = validationErrorMapper.mapFieldToErrorCode(path);
            }
        }

        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(mappedCode.getCode());
        errorResponse.setMessage(message);
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseWrapper> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        if (ex.getMessage() != null && ex.getMessage().contains(AppConstants.CONSTRAINT_DUPLICATE_TRANSACTION)) {
            errorResponse.setCode(ErrorCode.DUPLICATE_TRANSACTION.getCode());
            errorResponse.setMessage(messageService.getMessage("error.db.duplicate.transaction"));
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        errorResponse.setCode(ErrorCode.INTERNAL_ERROR.getCode());
        errorResponse.setMessage(messageService.getMessage("error.db.constraint.violation"));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseWrapper> handleNotFound(TransactionNotFoundException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ex.getErrorCode().getCode());
        errorResponse.setMessage(messageService.getMessage(ex.getMessageKey(), ex.getArgs()));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponseWrapper> handleInvalid(InvalidTransactionException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ex.getErrorCode().getCode());
        errorResponse.setMessage(messageService.getMessage(ex.getMessageKey(), ex.getArgs()));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<ErrorResponseWrapper> handleProvider(ProviderException ex) {
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ex.getErrorCode().getCode());
        errorResponse.setMessage(messageService.getMessage(ex.getMessageKey(), ex.getArgs()));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseWrapper> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponseWrapper errorResponse = new ErrorResponseWrapper();
        errorResponse.setCode(ErrorCode.INTERNAL_ERROR.getCode());
        errorResponse.setMessage(messageService.getMessage("error.db.unexpected"));
        errorResponse.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
