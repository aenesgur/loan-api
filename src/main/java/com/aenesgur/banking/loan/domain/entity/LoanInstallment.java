package com.aenesgur.banking.loan.domain.entity;

import com.aenesgur.banking.loan.domain.entity.base.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_installments")
@Getter
@Setter
public class LoanInstallment extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", referencedColumnName = "id", nullable = false)
    private Loan loan;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private Boolean isPaid = false;

    @Column(precision = 19, scale = 2)
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;
}
