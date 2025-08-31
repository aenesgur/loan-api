package com.aenesgur.banking.loan.model.dto;

import java.math.BigDecimal;

public record PaymentResult(int paidInstallmentCount, BigDecimal totalPaidAmount, boolean isLoanFullyPaid) { }
