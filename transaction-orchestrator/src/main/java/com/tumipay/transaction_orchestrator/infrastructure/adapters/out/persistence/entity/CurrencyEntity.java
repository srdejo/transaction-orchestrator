package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
public class CurrencyEntity {
    @Id
    @Column(name = "currency_code", length = 3, columnDefinition = "bpchar")
    private String currencyCode;

    @Column(name = "name", nullable = false)
    private String name;
}
