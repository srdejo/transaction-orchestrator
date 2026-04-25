package com.tumipay.transaction_orchestrator.application.mapper;

import com.tumipay.transaction_orchestrator.api.model.Customer;
import com.tumipay.transaction_orchestrator.application.ports.in.command.CreateTransactionCommand;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CreateTransactionCommand.CustomerCommand toCommand(Customer requestCustomer) {
        if (requestCustomer == null) return null;
        
        return new CreateTransactionCommand.CustomerCommand(
            requestCustomer.getDocumentType(),
            requestCustomer.getDocumentNumber(),
            requestCustomer.getPhoneCode(),
            requestCustomer.getPhoneNumber(),
            requestCustomer.getEmail(),
            requestCustomer.getFirstName(),
            requestCustomer.getSecondName(),
            requestCustomer.getLastName(),
            requestCustomer.getSecondLastName()
        );
    }
}
