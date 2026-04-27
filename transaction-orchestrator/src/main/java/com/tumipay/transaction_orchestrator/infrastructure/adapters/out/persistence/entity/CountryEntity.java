package com.tumipay.transaction_orchestrator.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class CountryEntity {
    @Id
    @Column(name = "country_code", length = 2, columnDefinition = "bpchar")
    private String countryCode;

    @Column(name = "name", nullable = false)
    private String name;
}
