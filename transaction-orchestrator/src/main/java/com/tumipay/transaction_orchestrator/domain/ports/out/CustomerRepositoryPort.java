package com.tumipay.transaction_orchestrator.domain.ports.out;

import com.tumipay.transaction_orchestrator.domain.model.Customer;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(String id);
}
