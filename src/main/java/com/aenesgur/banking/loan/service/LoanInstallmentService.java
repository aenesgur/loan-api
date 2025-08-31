package com.aenesgur.banking.loan.service;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;

import java.util.List;
import java.util.UUID;

public interface LoanInstallmentService {
    void saveAll(List<LoanInstallment> loanInstallments);
    List<LoanInstallment> findByLoanId(UUID loanId);
    List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(UUID loanId);
}
