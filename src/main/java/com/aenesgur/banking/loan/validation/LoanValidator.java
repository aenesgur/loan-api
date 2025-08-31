package com.aenesgur.banking.loan.validation;

import com.aenesgur.banking.loan.domain.entity.Customer;
import com.aenesgur.banking.loan.exception.model.LoanGeneralException;
import com.aenesgur.banking.loan.model.enumz.InstallmentPeriod;
import com.aenesgur.banking.loan.model.request.CreateLoanRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanValidator {
    public void validate(Customer customer, CreateLoanRequest request) {
        validateCreditLimit(customer, request.getAmount(), request.getInterestRate());
        validateNumberOfInstallments(request.getNumberOfInstallments());
    }

    private void validateCreditLimit(Customer customer, BigDecimal amount, BigDecimal interestRate) {
        BigDecimal totalLoanAmount = amount.multiply(BigDecimal.ONE.add(interestRate));
        if (customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(totalLoanAmount) < 0) {
            throw new LoanGeneralException("Credit limit is not enough");
        }
    }

    private void validateNumberOfInstallments(Integer numberOfInstallments) {
        if (!InstallmentPeriod.getValidPeriods().contains(numberOfInstallments)) {
            throw new LoanGeneralException("Invalid installment count: " + InstallmentPeriod.getValidPeriods());
        }
    }
}
