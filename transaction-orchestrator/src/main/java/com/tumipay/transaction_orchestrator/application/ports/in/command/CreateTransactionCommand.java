package com.tumipay.transaction_orchestrator.application.ports.in.command;

import java.time.LocalDateTime;

public record CreateTransactionCommand(
    String clientTransactionId,
    long amount,
    String currency,
    String countryCode,
    String paymentMethodId,
    String webhookUrl,
    String redirectUrl,
    CustomerCommand customer,
    String description,
    LocalDateTime expirationTime
) {
    public record CustomerCommand(
        String documentType,
        String documentNumber,
        String countryCallingCode,
        String phoneNumber,
        String email,
        String firstName,
        String middleName,
        String lastName,
        String secondLastName
    ) {}
}
