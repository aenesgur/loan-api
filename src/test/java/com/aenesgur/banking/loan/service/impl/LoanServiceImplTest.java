package com.aenesgur.banking.loan.service.impl;

import com.aenesgur.banking.loan.domain.entity.Customer;
import com.aenesgur.banking.loan.domain.entity.Loan;
import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import com.aenesgur.banking.loan.exception.model.LoanGeneralException;
import com.aenesgur.banking.loan.factory.PaymentPolicyFactory;
import com.aenesgur.banking.loan.model.dto.LoanDto;
import com.aenesgur.banking.loan.model.dto.LoanInstallmentDto;
import com.aenesgur.banking.loan.model.dto.PayLoanDto;
import com.aenesgur.banking.loan.model.request.CreateLoanRequest;
import com.aenesgur.banking.loan.policy.loanpayment.PaymentPolicy;
import com.aenesgur.banking.loan.repository.LoanRepository;
import com.aenesgur.banking.loan.service.CustomerService;
import com.aenesgur.banking.loan.service.LoanInstallmentService;
import com.aenesgur.banking.loan.validation.LoanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanInstallmentService loanInstallmentService;
    @Mock
    private LoanValidator loanValidator;
    @Mock
    private PaymentPolicyFactory paymentPolicyFactory;
    @Mock
    private PaymentPolicy mockPaymentPolicy;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Customer createMockCustomer(UUID id, BigDecimal creditLimit, BigDecimal usedCreditLimit) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCreditLimit(creditLimit);
        customer.setUsedCreditLimit(usedCreditLimit);
        return customer;
    }

    private Loan createMockLoan(UUID id, Customer customer, BigDecimal loanAmount, Integer numberOfInstallment) {
        Loan loan = new Loan();
        loan.setId(id);
        loan.setCustomer(customer);
        loan.setLoanAmount(loanAmount);
        loan.setNumberOfInstallment(numberOfInstallment);
        return loan;
    }

    private LoanInstallment createInstallment(Loan loan, BigDecimal amount, boolean isPaid) {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(UUID.randomUUID());
        installment.setLoan(loan);
        installment.setAmount(amount);
        installment.setPaidAmount(isPaid ? amount : BigDecimal.ZERO);
        installment.setIsPaid(isPaid);
        installment.setDueDate(LocalDate.now().minusMonths(1));
        return installment;
    }

    // --- 'create' method tests ---
    @Test
    void create_shouldCreateLoanAndInstallmentsSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.valueOf(10000), BigDecimal.ZERO);
        CreateLoanRequest request = CreateLoanRequest.builder()
                .amount(BigDecimal.valueOf(5000))
                .interestRate(BigDecimal.valueOf(0.1))
                .numberOfInstallments(12)
                .build();
        Loan savedLoan = createMockLoan(UUID.randomUUID(), customer, BigDecimal.valueOf(5500), 12);

        when(customerService.findById(customerId)).thenReturn(customer);
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act
        loanService.create(customerId, request);

        // Assert
        verify(loanValidator, times(1)).validate(customer, request);
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(loanInstallmentService, times(1)).saveAll(anyList());
        verify(customerService, times(1)).save(customer);
        assertEquals(BigDecimal.valueOf(5500), customer.getUsedCreditLimit());
    }

    @Test
    void create_shouldThrowException_whenValidationFails() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.valueOf(10000), BigDecimal.ZERO);
        CreateLoanRequest request = CreateLoanRequest.builder().build();

        when(customerService.findById(customerId)).thenReturn(customer);
        doThrow(new RuntimeException("Validation failed")).when(loanValidator).validate(customer, request);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanService.create(customerId, request));

        // Verify
        verify(loanRepository, never()).save(any(Loan.class));
        verify(loanInstallmentService, never()).saveAll(anyList());
    }

    // --- 'listInstallments' method tests ---
    @Test
    void listInstallments_shouldReturnInstallments_whenAuthorized() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.ZERO);
        Loan loan = createMockLoan(loanId, customer, BigDecimal.ZERO, 0);
        List<LoanInstallment> mockInstallments = List.of(
                createInstallment(loan, BigDecimal.valueOf(100), false),
                createInstallment(loan, BigDecimal.valueOf(100), false)
        );

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.findByLoanId(loanId)).thenReturn(mockInstallments);

        // Act
        List<LoanInstallmentDto> result = loanService.listInstallments(loanId, customerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockInstallments.get(0).getId(), result.get(0).getId());

        // Verify
        verify(loanRepository, times(1)).findById(loanId);
        verify(loanInstallmentService, times(1)).findByLoanId(loanId);
    }

    @Test
    void listInstallments_shouldThrowException_whenLoanIsNotFound() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(LoanGeneralException.class, () ->
                loanService.listInstallments(loanId, customerId));

        assertEquals("Loan not found.", exception.getMessage());

        // Verify
        verify(loanInstallmentService, never()).findByLoanId(any(UUID.class));
    }

    @Test
    void listInstallments_shouldThrowException_whenCustomerIsNotAuthorized() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID anotherCustomerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.ZERO);
        Loan loan = createMockLoan(loanId, customer, BigDecimal.ZERO, 0);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        // Act & Assert
        Exception exception = assertThrows(LoanGeneralException.class, () ->
                loanService.listInstallments(loanId, anotherCustomerId));

        assertEquals("You are not authorized to list the installments of this loan.", exception.getMessage());

        // Verify
        verify(loanInstallmentService, never()).findByLoanId(any(UUID.class));
    }

    // --- 'listLoans' method tests ---
    @Test
    void listLoans_shouldReturnLoanDtos_whenCustomerExistsAndHasLoans() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.ZERO);
        List<Loan> mockLoans = List.of(
                createMockLoan(UUID.randomUUID(), customer, BigDecimal.valueOf(2500), 12),
                createMockLoan(UUID.randomUUID(), customer, BigDecimal.valueOf(3000), 12)
        );

        when(customerService.findById(customerId)).thenReturn(customer);
        when(loanRepository.findByCustomerId(customerId)).thenReturn(mockLoans);

        // Act
        List<LoanDto> result = loanService.listLoans(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify
        verify(customerService, times(1)).findById(customerId);
        verify(loanRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void listLoans_shouldReturnEmptyList_whenCustomerExistsButHasNoLoans() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.ZERO);

        when(customerService.findById(customerId)).thenReturn(customer);
        when(loanRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // Act
        List<LoanDto> result = loanService.listLoans(customerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(customerService, times(1)).findById(customerId);
        verify(loanRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void listLoans_shouldThrowException_whenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        doThrow(new LoanGeneralException("Customer not found.")).when(customerService).findById(customerId);

        // Act & Assert
        Exception exception = assertThrows(LoanGeneralException.class, () ->
                loanService.listLoans(customerId));

        assertEquals("Customer not found.", exception.getMessage());

        // Verify
        verify(loanRepository, never()).findByCustomerId(any(UUID.class));
    }

    // --- 'pay' method tests ---
    @Test
    void pay_shouldFullyPayLoan_whenPaymentIsSufficient() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.valueOf(2000));
        Loan loan = createMockLoan(loanId, customer, BigDecimal.ZERO, 0);
        List<LoanInstallment> installments = List.of(
                createInstallment(loan, BigDecimal.valueOf(400), false),
                createInstallment(loan, BigDecimal.valueOf(400), false)
        );

        when(loanRepository.findByIdAndIsPaidFalseWithLock(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId)).thenReturn(installments);
        when(paymentPolicyFactory.getPolicy(any(LoanInstallment.class))).thenReturn(mockPaymentPolicy);
        when(mockPaymentPolicy.calculate(any(LoanInstallment.class))).thenReturn(BigDecimal.valueOf(500));
        when(customerService.findById(customerId)).thenReturn(customer);

        // Act
        PayLoanDto result = loanService.pay(loanId, BigDecimal.valueOf(1000), customerId);

        // Assert
        assertEquals(2, result.getPaidInstallmentCount());
        assertEquals(BigDecimal.valueOf(1000), result.getTotalPaidAmount());
        assertTrue(result.isLoanFullyPaid());

        // Verify
        verify(loanRepository).findByIdAndIsPaidFalseWithLock(loanId);
        verify(loanInstallmentService).findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId);
        verify(loanInstallmentService).saveAll(argThat(list ->
                list.stream().allMatch(LoanInstallment::getIsPaid)
        ));
        verify(loanRepository).save(argThat(l -> l.getIsPaid().equals(true)));
        verify(customerService).save(argThat(c -> c.getUsedCreditLimit().compareTo(BigDecimal.valueOf(1000)) == 0));
    }

    @Test
    void pay_shouldPartiallyPayLoan_whenPaymentIsNotSufficient() {
        // Arrange
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Customer customer = createMockCustomer(customerId, BigDecimal.ZERO, BigDecimal.valueOf(2000));
        Loan loan = createMockLoan(loanId, customer, BigDecimal.ZERO, 0);
        List<LoanInstallment> installments = List.of(
                createInstallment(loan, BigDecimal.valueOf(400), false),
                createInstallment(loan, BigDecimal.valueOf(400), false)
        );

        when(loanRepository.findByIdAndIsPaidFalseWithLock(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentService.findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId)).thenReturn(installments);
        when(paymentPolicyFactory.getPolicy(any(LoanInstallment.class))).thenReturn(mockPaymentPolicy);
        when(mockPaymentPolicy.calculate(any(LoanInstallment.class))).thenReturn(BigDecimal.valueOf(500));
        when(customerService.findById(customerId)).thenReturn(customer);

        // Act
        PayLoanDto result = loanService.pay(loanId, BigDecimal.valueOf(500), customerId);

        // Assert
        assertEquals(1, result.getPaidInstallmentCount());
        assertEquals(BigDecimal.valueOf(500), result.getTotalPaidAmount());
        assertFalse(result.isLoanFullyPaid());

        // Verify
        verify(loanRepository).findByIdAndIsPaidFalseWithLock(loanId);
        verify(loanInstallmentService).findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(loanId);
        verify(loanInstallmentService).saveAll(argThat(list ->
                list.get(0).getIsPaid().equals(true) && list.get(1).getIsPaid().equals(false)
        ));
        verify(loanRepository).save(argThat(l -> l.getIsPaid().equals(false)));
        verify(customerService).save(argThat(c -> c.getUsedCreditLimit().compareTo(BigDecimal.valueOf(1500)) == 0));
    }
}