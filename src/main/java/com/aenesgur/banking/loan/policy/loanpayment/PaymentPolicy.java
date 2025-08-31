package com.aenesgur.banking.loan.policy.loanpayment;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;

import java.math.BigDecimal;

public interface PaymentPolicy {
    BigDecimal calculate(LoanInstallment installment);
}
