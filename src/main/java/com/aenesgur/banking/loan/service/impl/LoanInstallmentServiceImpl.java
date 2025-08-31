package com.aenesgur.banking.loan.service.impl;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import com.aenesgur.banking.loan.repository.LoanInstallmentRepository;
import com.aenesgur.banking.loan.service.LoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanInstallmentServiceImpl implements LoanInstallmentService {
    private final LoanInstallmentRepository loanInstallmentRepository;
    @Override
    public void saveAll(List<LoanInstallment> loanInstallments) {
        loanInstallmentRepository.saveAll(loanInstallments);
    }

    @Override
    public List<LoanInstallment> findByLoanId(UUID loanId) {
        return loanInstallmentRepository.findByLoanId(loanId);
    }

    @Override
    public List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(UUID loanId) {
        return loanInstallmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId);
    }

}
