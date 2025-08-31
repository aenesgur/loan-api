package com.aenesgur.banking.loan.service.impl;

import com.aenesgur.banking.loan.domain.entity.Customer;
import com.aenesgur.banking.loan.exception.model.LoanGeneralException;
import com.aenesgur.banking.loan.repository.CustomerRepository;
import com.aenesgur.banking.loan.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    @Override
    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new LoanGeneralException("User not found"));
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }
}