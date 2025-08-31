package com.aenesgur.banking.loan.repository;

import com.aenesgur.banking.loan.domain.entity.LoanInstallment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, UUID> {
    List<LoanInstallment> findByLoanId(UUID loanId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.isPaid = false ORDER BY li.dueDate ASC")
    List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDateAscWithLock(UUID loanId);
}
