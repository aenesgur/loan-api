package com.aenesgur.banking.loan.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PayLoanResponse {
    private int paidInstallmentCount;
    private BigDecimal totalPaidAmount;
    private boolean isLoanFullyPaid;
}
