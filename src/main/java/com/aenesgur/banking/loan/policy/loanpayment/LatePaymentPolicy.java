package com.aenesgur.banking.loan.policy.loanpayment;

import com.aenesgur.banking.loan.configuration.PaymentPolicyProperties;
import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LatePaymentPolicy implements PaymentPolicy {
    private final PaymentPolicyProperties paymentPolicyProperties;

    public BigDecimal calculate(LoanInstallment installment) {
        long daysLate = Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), installment.getDueDate()));
        BigDecimal penalty = installment.getAmount().multiply(paymentPolicyProperties.getLateRate())
                .multiply(BigDecimal.valueOf(daysLate)).setScale(2, RoundingMode.HALF_UP);
        installment.setPenaltyAmount(penalty);
        installment.setRewardAmount(BigDecimal.ZERO);
        return installment.getAmount().add(penalty);
    }
}