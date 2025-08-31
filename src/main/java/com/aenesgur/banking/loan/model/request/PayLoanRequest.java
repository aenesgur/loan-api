package com.aenesgur.banking.loan.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayLoanRequest {
    @NotNull(message = "Payment amount cannot be empty.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than 0.")
    private BigDecimal amount;
}