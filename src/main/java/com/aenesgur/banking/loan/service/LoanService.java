package com.aenesgur.banking.loan.service;

import com.aenesgur.banking.loan.model.dto.LoanDto;
import com.aenesgur.banking.loan.model.dto.LoanInstallmentDto;
import com.aenesgur.banking.loan.model.dto.PayLoanDto;
import com.aenesgur.banking.loan.model.request.CreateLoanRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LoanService {
    void create(UUID customerId, CreateLoanRequest createLoanRequest);
    List<LoanDto> listLoans(UUID customerId);
    List<LoanInstallmentDto> listInstallments(UUID loanId, UUID customerId);
    PayLoanDto pay(UUID loanId, BigDecimal paymentAmount, UUID customerId);
}
