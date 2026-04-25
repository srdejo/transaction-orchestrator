package com.tumipay.transaction_orchestrator.infrastructure.adapters.inbound.rest.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.api.model.ErrorResponseWrapper;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public IdempotencyAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.registerModule(new org.openapitools.jackson.nullable.JsonNullableModule());
    }

    @Around("execution(* com.tumipay.transaction_orchestrator.infrastructure.adapters.inbound.rest.TransactionController.createTransaction(..)) && args(request)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint, CreateTransactionRequest request) throws Throwable {
        String customId = request.getClientTransactionId();
        if (customId == null || customId.trim().isEmpty()) {
            return joinPoint.proceed();
        }

        String redisKey = "idempotency:tx:" + customId;
        
        // Use SET NX EX to try to acquire lock
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(redisKey, "IN_PROGRESS", 24, TimeUnit.HOURS);
        
        if (Boolean.FALSE.equals(acquired)) {
            // Key exists. What is the value?
            String value = redisTemplate.opsForValue().get(redisKey);
            if ("IN_PROGRESS".equals(value)) {
                log.warn("Concurrent request detected for customId: {}", customId);
                ErrorResponseWrapper error = new ErrorResponseWrapper();
                error.setCode("409");
                error.setMessage("Transaction is already being processed");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } else if (value != null) {
                log.info("Returning cached response for customId: {}", customId);
                TransactionResponseWrapper cachedResponse = objectMapper.readValue(value, TransactionResponseWrapper.class);
                return ResponseEntity.ok(cachedResponse);
            }
        }

        try {
            // Execute the actual controller method
            Object result = joinPoint.proceed();
            
            // If it succeeded and returned a ResponseEntity<TransactionResponseWrapper>
            if (result instanceof ResponseEntity<?> responseEntity) {
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() instanceof TransactionResponseWrapper wrapper) {
                    String jsonResponse = objectMapper.writeValueAsString(wrapper);
                    redisTemplate.opsForValue().set(redisKey, jsonResponse, 24, TimeUnit.HOURS);
                } else {
                    // If failed, remove the key so they can retry
                    redisTemplate.delete(redisKey);
                }
            }
            return result;
        } catch (Exception e) {
            // On exception, delete the key to allow retrying
            redisTemplate.delete(redisKey);
            throw e;
        }
    }
}
