package com.aenesgur.banking.loan.model.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class CreateLoanRequest {
    @NotNull(message = "Loan amount is mandatory.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Loan amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Interest rate is mandatory.")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1.")
    @DecimalMax(value = "0.5", message = "Interest rate can be at most 0.5.")
    private BigDecimal interestRate;

    @NotNull(message = "Number of installments is mandatory.")
    @Min(value = 6, message = "Number of installments can be at least 6.")
    private Integer numberOfInstallments;
}
