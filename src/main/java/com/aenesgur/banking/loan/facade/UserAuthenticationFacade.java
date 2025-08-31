package com.aenesgur.banking.loan.facade;

import com.aenesgur.banking.loan.service.AuthService;
import com.aenesgur.banking.loan.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFacade {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public String authenticateUserAndGenerateAccessToken(String username, String password) {
        Authentication authentication = authService.authenticateUser(username, password);
        return jwtTokenProvider.generateToken(authentication);
    }
}
