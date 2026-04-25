package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.application.service.AsyncPaymentProcessor;
import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Money;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final AsyncPaymentProcessor asyncPaymentProcessor;

    public CreateTransactionService(TransactionRepositoryPort transactionRepository, 
                                    AsyncPaymentProcessor asyncPaymentProcessor) {
        this.transactionRepository = transactionRepository;
        this.asyncPaymentProcessor = asyncPaymentProcessor;
    }

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        Customer customer = new Customer(
            DocumentType.valueOf(command.customer().documentType()),
            command.customer().documentNumber(),
            command.customer().countryCallCode(),
            command.customer().phone(),
            command.customer().email(),
            command.customer().firstName(),
            command.customer().middleName(),
            command.customer().lastName(),
            command.customer().secondLastName()
        );

        Money money = new Money(command.amount(), new Currency(command.currency()));
        CountryCode countryCode = new CountryCode(command.countryCode());
        PaymentMethod paymentMethod = new PaymentMethod(command.paymentMethodId());

        Transaction transaction = new Transaction(
            command.clientTransactionId(),
            money,
            countryCode,
            paymentMethod,
            command.webhookUrl(),
            command.redirectUrl(),
            customer,
            command.description(),
            command.expirationTime()
        );
        
        transaction.assignId(UUID.randomUUID().toString());
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Disparar procesamiento asíncrono
        asyncPaymentProcessor.process(savedTransaction);
        
        // Retornamos inmediatamente el estado inicial PENDING al cliente
        return savedTransaction;
    }
}
