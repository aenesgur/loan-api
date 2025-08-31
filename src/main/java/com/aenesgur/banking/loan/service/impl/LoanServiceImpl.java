package com.aenesgur.banking.loan.service.impl;

import com.aenesgur.banking.loan.domain.entity.Customer;
import com.aenesgur.banking.loan.domain.entity.Loan;
import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import com.aenesgur.banking.loan.exception.model.LoanGeneralException;
import com.aenesgur.banking.loan.factory.PaymentPolicyFactory;
import com.aenesgur.banking.loan.model.dto.LoanDto;
import com.aenesgur.banking.loan.model.dto.LoanInstallmentDto;
import com.aenesgur.banking.loan.model.dto.PayLoanDto;
import com.aenesgur.banking.loan.model.dto.PaymentResult;
import com.aenesgur.banking.loan.model.request.CreateLoanRequest;
import com.aenesgur.banking.loan.repository.LoanRepository;
import com.aenesgur.banking.loan.service.CustomerService;
import com.aenesgur.banking.loan.service.LoanInstallmentService;
import com.aenesgur.banking.loan.service.LoanService;
import com.aenesgur.banking.loan.validation.LoanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final CustomerService customerService;
    private final LoanRepository loanRepository;
    private final LoanInstallmentService loanInstallmentService;
    private final LoanValidator loanValidator;
    private final PaymentPolicyFactory paymentPolicyFactory;

    @Transactional
    @Override
    public void create(UUID customerId, CreateLoanRequest request) {
        Customer customer = customerService.findById(customerId);
        loanValidator.validate(customer, request);
        Loan savedLoan = loanRepository.save(createNewLoanEntity(customer, request));
        List<LoanInstallment> installments = createInstallments(savedLoan);
        loanInstallmentService.saveAll(installments);
        updateCustomerUsedCredit(customer, savedLoan.getLoanAmount());
    }

    private Loan createNewLoanEntity(Customer customer, CreateLoanRequest request) {
        Loan newLoan = new Loan();
        newLoan.setCustomer(customer);
        newLoan.setLoanAmount(request.getAmount().multiply(BigDecimal.ONE.add(request.getInterestRate())));
        newLoan.setNumberOfInstallment(request.getNumberOfInstallments());
        return newLoan;
    }

    private List<LoanInstallment> createInstallments(Loan loan) {
        BigDecimal installmentAmount = loan.getLoanAmount().divide(BigDecimal.valueOf(loan.getNumberOfInstallment()), 2, RoundingMode.HALF_UP);
        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 1; i <= loan.getNumberOfInstallment(); i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1));
            installments.add(installment);
        }
        return installments;
    }

    private void updateCustomerUsedCredit(Customer customer, BigDecimal amount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(amount));
        customerService.save(customer);
    }

    @Override
    public List<LoanDto> listLoans(UUID customerId) {
        validateCustomer(customerId);
        List<Loan> loans = loanRepository.findByCustomerId(customerId);
        return loans.stream()
                .map(LoanDto::fromEntity)
                .toList();
    }

    public void validateCustomer(UUID customerId) {
        customerService.findById(customerId);
    }

    @Override
    public List<LoanInstallmentDto> listInstallments(UUID loanId, UUID customerId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanGeneralException("Loan not found."));

        if (!loan.getCustomer().getId().equals(customerId)) {
            log.warn("Unauthorized attempt to list installments for loan {} by customer {}", loanId, customerId);
            throw new LoanGeneralException("You are not authorized to list the installments of this loan.");
        }

        List<LoanInstallment> installments = loanInstallmentService.findByLoanId(loanId);
        return installments.stream()
                .map(LoanInstallmentDto::fromEntity)
                .toList();
    }

    //TODO: may add transactionId for applying idempotency
    @Transactional
    public PayLoanDto pay(UUID loanId, BigDecimal paymentAmount, UUID requestingCustomerId) {
        Loan loan = getUnpaidLoanAndValidateAccessWithLock(loanId, requestingCustomerId);
        List<LoanInstallment> unpaidInstallments = getUnpaidInstallmentsWithLock(loanId);

        if (unpaidInstallments.isEmpty()) {
            log.info("Loan {} is already fully paid", loanId);
            return new PayLoanDto(0, BigDecimal.ZERO, true);
        }

        PaymentResult result = processInstallments(unpaidInstallments, paymentAmount);

        loanInstallmentService.saveAll(unpaidInstallments);
        Customer customer = customerService.findById(loan.getCustomer().getId());
        updateLoanAndCustomer(loan, customer, result.totalPaidAmount(), result.isLoanFullyPaid());

        return new PayLoanDto(result.paidInstallmentCount(), result.totalPaidAmount(), result.isLoanFullyPaid());
    }

    private PaymentResult processInstallments(List<LoanInstallment> unpaidInstallments, BigDecimal paymentAmount) {
        BigDecimal remainingPaymentAmount = paymentAmount;
        int paidInstallmentCount = 0;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;

        for (LoanInstallment installment : unpaidInstallments) {
            if (!shouldBePaid(installment)) break;

            BigDecimal finalPaymentAmount = paymentPolicyFactory.getPolicy(installment).calculate(installment);
            if (remainingPaymentAmount.compareTo(finalPaymentAmount) >= 0) {
                updateInstallmentAsPaid(installment, finalPaymentAmount);

                paidInstallmentCount++;
                totalPaidAmount = totalPaidAmount.add(finalPaymentAmount);
                remainingPaymentAmount = remainingPaymentAmount.subtract(finalPaymentAmount);
            } else {
                break;
            }
        }

        boolean isLoanFullyPaid = unpaidInstallments.size() == paidInstallmentCount;
        return new PaymentResult(paidInstallmentCount, totalPaidAmount, isLoanFullyPaid);
    }

    private boolean shouldBePaid(LoanInstallment installment) {
        LocalDate threeMonthsFromNow = LocalDate.now()
                .plusMonths(3)
                .with(TemporalAdjusters.lastDayOfMonth());
        return !installment.getDueDate().isAfter(threeMonthsFromNow);
    }

    private Loan getUnpaidLoanAndValidateAccessWithLock(UUID loanId, UUID requestingCustomerId) {
        Loan loan = loanRepository.findByIdAndIsPaidFalseWithLock(loanId)
                .orElseThrow(() -> new LoanGeneralException("The loan was not found or has already been paid."));

        if (!loan.getCustomer().getId().equals(requestingCustomerId)) {
            log.warn("Unauthorized repayment attempt for loan {} by customer {}", loanId, requestingCustomerId);
            throw new LoanGeneralException("You are not authorized to repay this loan.");
        }
        return loan;
    }

    private List<LoanInstallment> getUnpaidInstallmentsWithLock(UUID loanId) {
        return loanInstallmentService.findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId);
    }

    private void updateLoanAndCustomer(Loan loan, Customer customer, BigDecimal totalPaidAmount, boolean isLoanFullyPaid) {
        if (isLoanFullyPaid) {
            loan.setIsPaid(true);
        }
        loanRepository.save(loan);

        //TODO: Add retry for OptimisticLockException
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(totalPaidAmount));
        customerService.save(customer);
    }

    private void updateInstallmentAsPaid(LoanInstallment installment, BigDecimal finalPaymentAmount) {
        installment.setIsPaid(true);
        installment.setPaidAmount(finalPaymentAmount);
        installment.setPaymentDate(LocalDateTime.now());
    }
}
