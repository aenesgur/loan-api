package com.aenesgur.banking.loan.model.response;

import com.aenesgur.banking.loan.model.dto.LoanInstallmentDto;
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
public class LoanInstallmentResponse {
    private UUID id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private Boolean isPaid;
    private UUID loanId;

    public static LoanInstallmentResponse fromDto(LoanInstallmentDto installmentDto) {
        return LoanInstallmentResponse.builder()
                .id(installmentDto.getId())
                .amount(installmentDto.getAmount())
                .paidAmount(installmentDto.getPaidAmount())
                .dueDate(installmentDto.getDueDate())
                .paymentDate(installmentDto.getPaymentDate())
                .isPaid(installmentDto.getIsPaid())
                .loanId(installmentDto.getLoanId())
                .build();
    }
}
