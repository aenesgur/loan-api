package com.aenesgur.banking.loan.factory;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import com.aenesgur.banking.loan.policy.loanpayment.EarlyPaymentPolicy;
import com.aenesgur.banking.loan.policy.loanpayment.LatePaymentPolicy;
import com.aenesgur.banking.loan.policy.loanpayment.OnTimePaymentPolicy;
import com.aenesgur.banking.loan.policy.loanpayment.PaymentPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PaymentPolicyFactory {
    private final EarlyPaymentPolicy earlyPaymentPolicy;
    private final LatePaymentPolicy latePaymentPolicy;
    private final OnTimePaymentPolicy onTimePaymentPolicy;

    public PaymentPolicy getPolicy(LoanInstallment installment) {
        long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), installment.getDueDate());
        if (daysDifference > 0) return earlyPaymentPolicy;
        if (daysDifference < 0) return latePaymentPolicy;
        return onTimePaymentPolicy;
    }
}
