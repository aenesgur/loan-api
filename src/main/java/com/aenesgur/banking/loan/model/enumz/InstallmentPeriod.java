package com.aenesgur.banking.loan.model.enumz;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum InstallmentPeriod {
    SIX_MONTHS(6),
    NINE_MONTHS(9),
    TWELVE_MONTHS(12),
    TWENTY_FOUR_MONTHS(24);

    private final int numberOfInstallments;

    InstallmentPeriod(int numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public static List<Integer> getValidPeriods() {
        return Arrays.stream(InstallmentPeriod.values())
                .map(InstallmentPeriod::getNumberOfInstallments)
                .collect(Collectors.toList());
    }
}
