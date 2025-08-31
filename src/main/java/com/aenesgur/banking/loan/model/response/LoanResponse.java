package com.aenesgur.banking.loan.model.response;

import com.aenesgur.banking.loan.model.dto.LoanDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class LoanResponse {
    private UUID id;
    private UUID customerId;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private Boolean isPaid;

    public static LoanResponse fromDto(LoanDto loanDto) {
        return LoanResponse.builder()
                .id(loanDto.getId())
                .customerId(loanDto.getCustomerId())
                .loanAmount(loanDto.getLoanAmount())
                .numberOfInstallment(loanDto.getNumberOfInstallment())
                .isPaid(loanDto.getIsPaid())
                .build();
    }
}
