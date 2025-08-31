package com.aenesgur.banking.loan.repository;

import com.aenesgur.banking.loan.domain.entity.Loan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :loanId and l.isPaid = false")
    Optional<Loan> findByIdAndIsPaidFalseWithLock(@Param("loanId") UUID loanId);

    List<Loan> findByCustomerId(UUID customerId);

    Optional<Loan> findByIdAndIsPaidFalse(UUID loanId);
}
