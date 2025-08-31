package com.aenesgur.banking.loan.service;

import com.aenesgur.banking.loan.domain.entity.Customer;

import java.util.UUID;

public interface CustomerService {
    Customer findById(UUID id);
    void save(Customer customer);
}
