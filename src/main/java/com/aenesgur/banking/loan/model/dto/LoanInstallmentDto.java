package com.aenesgur.banking.loan.model.dto;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LoanInstallmentDto {
    private UUID id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private Boolean isPaid;
    private UUID loanId;

    public static LoanInstallmentDto fromEntity(LoanInstallment installment) {
        return LoanInstallmentDto.builder()
                .id(installment.getId())
                .amount(installment.getAmount())
                .paidAmount(installment.getPaidAmount())
                .dueDate(installment.getDueDate())
                .paymentDate(installment.getPaymentDate())
                .isPaid(installment.getIsPaid())
                .loanId(installment.getLoan().getId())
                .build();
    }
}
