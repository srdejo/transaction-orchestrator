package com.tumipay.transaction_orchestrator.application.usecase;

import com.tumipay.transaction_orchestrator.application.ports.in.CreateTransactionUseCase;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import com.tumipay.transaction_orchestrator.application.service.AsyncPaymentProcessor;
import com.tumipay.transaction_orchestrator.domain.exception.BusinessException;
import com.tumipay.transaction_orchestrator.domain.exception.ErrorCode;
import com.tumipay.transaction_orchestrator.domain.model.Customer;
import com.tumipay.transaction_orchestrator.domain.model.PaymentMethod;
import com.tumipay.transaction_orchestrator.domain.model.Transaction;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.CountryCode;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.Currency;
import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;
import com.tumipay.transaction_orchestrator.domain.ports.out.ReferenceDataPort;
import com.tumipay.transaction_orchestrator.domain.ports.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateTransactionService implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final AsyncPaymentProcessor asyncPaymentProcessor;
    private final ReferenceDataPort referenceDataPort;

    public CreateTransactionService(TransactionRepositoryPort transactionRepository,
            AsyncPaymentProcessor asyncPaymentProcessor,
            ReferenceDataPort referenceDataPort) {
        this.transactionRepository = transactionRepository;
        this.asyncPaymentProcessor = asyncPaymentProcessor;
        this.referenceDataPort = referenceDataPort;
    }

    @Override
    public Transaction execute(CreateTransactionCommand command) {
        if (!referenceDataPort.isValidCountry(command.countryCode())) {
            throw new BusinessException(
                    ErrorCode.INVALID_COUNTRY,
                    "error.001.detail",
                    command.countryCode());
        }
        if (!referenceDataPort.isValidCurrency(command.currency())) {
            throw new BusinessException(
                    ErrorCode.INVALID_CURRENCY,
                    "error.002.detail",
                    command.currency());
        }
        if (!referenceDataPort.isValidPaymentMethod(command.paymentMethodId())) {
            throw new BusinessException(
                    ErrorCode.INVALID_PAYMENT_METHOD,
                    "error.004.detail",
                    command.paymentMethodId());
        }

        Customer customer = new Customer(
                DocumentType.valueOf(command.customer().documentType()),
                command.customer().documentNumber(),
                command.customer().countryCallingCode(),
                command.customer().phoneNumber(),
                command.customer().email(),
                command.customer().firstName(),
                command.customer().middleName(),
                command.customer().lastName(),
                command.customer().secondLastName());

        Currency currency = new Currency(command.currency());
        CountryCode countryCode = new CountryCode(command.countryCode());
        PaymentMethod paymentMethod = new PaymentMethod(command.paymentMethodId());

        Transaction transaction = new Transaction(
                command.clientTransactionId(),
                command.amount(),
                currency,
                countryCode,
                paymentMethod,
                command.webhookUrl(),
                command.redirectUrl(),
                customer,
                command.description(),
                command.expirationTime());

        transaction.assignId(UUID.randomUUID().toString());
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Disparar procesamiento asíncrono
        asyncPaymentProcessor.process(savedTransaction);

        // Retornamos inmediatamente el estado inicial PENDING al cliente
        return savedTransaction;
    }
}
