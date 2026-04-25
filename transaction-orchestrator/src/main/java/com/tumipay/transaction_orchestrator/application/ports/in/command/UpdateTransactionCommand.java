package com.tumipay.transaction_orchestrator.application.ports.in.command;

public record UpdateTransactionCommand(
    String status,
    String providerTransactionId,
    String message
) {}
