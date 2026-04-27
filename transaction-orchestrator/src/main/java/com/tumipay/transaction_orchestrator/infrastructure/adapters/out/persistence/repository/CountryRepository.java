package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.repository;

import com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {
}
