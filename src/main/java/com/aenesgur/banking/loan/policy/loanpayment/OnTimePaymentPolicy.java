package com.aenesgur.banking.loan.policy.loanpayment;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OnTimePaymentPolicy implements PaymentPolicy {
    public BigDecimal calculate(LoanInstallment installment) {
        installment.setRewardAmount(BigDecimal.ZERO);
        installment.setPenaltyAmount(BigDecimal.ZERO);
        return installment.getAmount();
    }
}
