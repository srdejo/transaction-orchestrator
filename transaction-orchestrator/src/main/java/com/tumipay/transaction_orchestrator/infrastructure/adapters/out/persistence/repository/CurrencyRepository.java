package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
}
