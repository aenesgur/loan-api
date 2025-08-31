package com.aenesgur.banking.loan.model.dto;

import com.aenesgur.banking.loan.domain.entity.Loan;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LoanDto {
    private UUID id;
    private UUID customerId;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private Boolean isPaid;

    public static LoanDto fromEntity(Loan loan) {
        return LoanDto.builder()
                .id(loan.getId())
                .customerId(loan.getCustomer().getId())
                .loanAmount(loan.getLoanAmount())
                .numberOfInstallment(loan.getNumberOfInstallment())
                .isPaid(loan.getIsPaid())
                .build();
    }
}
