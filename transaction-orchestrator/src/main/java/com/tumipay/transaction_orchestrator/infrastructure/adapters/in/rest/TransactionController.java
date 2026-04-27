package com.tumipay.transaction_orchestrator.infrastructure.adapters.in.rest;

import com.tumipay.transaction_orchestrator.api.TransactionsApi;
import com.tumipay.transaction_orchestrator.api.model.CreateTransactionRequest;
import com.tumipay.transaction_orchestrator.api.model.TransactionResponseWrapper;
import com.tumipay.transaction_orchestrator.api.model.UpdateTransactionRequest;
import com.tumipay.transaction_orchestrator.application.mapper.TransactionMapper;
import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.GetTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.UpdateTransactionUseCase;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@Tag(name = "Transactions")
public class TransactionController implements TransactionsApi {

    private final CreateTransactionUseCase createUseCase;
    private final GetTransactionUseCase getUseCase;
    private final UpdateTransactionUseCase updateUseCase;
    private final TransactionMapper mapper;

    public TransactionController(CreateTransactionUseCase createUseCase,
            GetTransactionUseCase getUseCase,
            UpdateTransactionUseCase updateUseCase,
            TransactionMapper mapper) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.updateUseCase = updateUseCase;
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

    @Override
    public ResponseEntity<TransactionResponseWrapper> updateTransaction(
            @PathVariable("transaction_id") UUID transactionId, @Valid @RequestBody UpdateTransactionRequest request) {
        Transaction transaction = updateUseCase.execute(transactionId.toString(), mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(transaction));
    }
}
