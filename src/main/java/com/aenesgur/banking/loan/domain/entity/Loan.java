package com.aenesgur.banking.loan.domain.entity;

import com.aenesgur.banking.loan.domain.entity.base.BaseAuditingEntity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Getter
@Setter
public class Loan extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private Integer numberOfInstallment;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments;
}
