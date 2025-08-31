package com.aenesgur.banking.loan.controller;

import com.aenesgur.banking.loan.model.dto.LoanDto;
import com.aenesgur.banking.loan.model.dto.LoanInstallmentDto;
import com.aenesgur.banking.loan.model.dto.PayLoanDto;
import com.aenesgur.banking.loan.model.request.CreateLoanRequest;
import com.aenesgur.banking.loan.model.request.PayLoanRequest;
import com.aenesgur.banking.loan.model.response.LoanInstallmentResponse;
import com.aenesgur.banking.loan.model.response.LoanResponse;
import com.aenesgur.banking.loan.model.response.PayLoanResponse;
import com.aenesgur.banking.loan.service.LoanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<Void> createCustomerLoan(
            @RequestAttribute String userId,
            @RequestAttribute("resolvedCustomerId") String customerId,
            @Valid @RequestBody CreateLoanRequest request) {
        loanService.create(UUID.fromString(customerId), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @SecurityRequirement(name = "Authorization")
    public List<LoanResponse> listLoans(
            @RequestAttribute String userId,
            @RequestAttribute("resolvedCustomerId") String customerId) {
        List<LoanDto> loanDtoList = loanService.listLoans(UUID.fromString(customerId));
        return loanDtoList.stream()
                .map(LoanResponse::fromDto)
                .toList();
    }

    @GetMapping("/{loanId}/installments")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @SecurityRequirement(name = "Authorization")
    public List<LoanInstallmentResponse> listInstallments(
            @RequestAttribute String userId,
            @RequestAttribute("resolvedCustomerId") String customerId,
            @PathVariable UUID loanId) {
        List<LoanInstallmentDto> loanInstallments = loanService.listInstallments(loanId, UUID.fromString(customerId));
        return loanInstallments.stream()
                .map(LoanInstallmentResponse::fromDto)
                .toList();
    }

    @PostMapping("/{loanId}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @SecurityRequirement(name = "Authorization")
    public PayLoanResponse payLoan(
            @RequestAttribute String userId,
            @RequestAttribute("resolvedCustomerId") String customerId,
            @PathVariable String loanId,
            @Valid @RequestBody PayLoanRequest request) {
        PayLoanDto payLoad = loanService.pay(UUID.fromString(loanId), request.getAmount(), UUID.fromString(customerId));
        return PayLoanResponse.builder()
                .isLoanFullyPaid(payLoad.isLoanFullyPaid())
                .totalPaidAmount(payLoad.getTotalPaidAmount())
                .paidInstallmentCount(payLoad.getPaidInstallmentCount())
                .build();
    }
}
