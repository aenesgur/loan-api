package com.aenesgur.banking.loan.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PayLoanDto {
    private int paidInstallmentCount;
    private BigDecimal totalPaidAmount;
    private boolean isLoanFullyPaid;
}
