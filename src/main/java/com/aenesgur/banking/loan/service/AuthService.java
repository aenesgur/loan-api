package com.aenesgur.banking.loan.service;

import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication authenticateUser(String username, String password);
}
