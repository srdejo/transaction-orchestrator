package com.tumipay.transaction_orchestrator.infrastructure.adapters.in.rest;

import com.tumipay.transaction_orchestrator.api.TransactionsApi;
import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import com.tumipay.transaction_orchestrator.application.mapper.TransactionMapper;
import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.GetTransactionUseCase;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Transactions")
public class TransactionController implements TransactionsApi {

    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionUseCase getUseCase;
    private final TransactionMapper mapper;

    public TransactionController(CreateTransactionUseCase createUseCase,
            GetTransactionUseCase getUseCase,
            TransactionMapper mapper) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<TransactionResponseWrapper> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = createUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @Override
    public ResponseEntity<TransactionResponseWrapper> getTransaction(
            @PathVariable("transaction_id") UUID transactionId) {
        Transaction transaction = getUseCase.execute(transactionId.toString());
        return ResponseEntity.ok(mapper.toResponse(transaction));
    }

}
