package com.aenesgur.banking.loan.domain.entity;

import com.aenesgur.banking.loan.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal creditLimit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal usedCreditLimit;
}
