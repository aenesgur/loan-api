package com.aenesgur.banking.loan.controller.auth;

import com.aenesgur.banking.loan.facade.UserAuthenticationFacade;
import com.aenesgur.banking.loan.model.request.LoginRequest;
import com.aenesgur.banking.loan.model.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserAuthenticationFacade userAuthenticationFacade;
    @PostMapping("/login")
    public TokenResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userAuthenticationFacade.authenticateUserAndGenerateAccessToken(loginRequest.getUsername(), loginRequest.getPassword());
        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }
}
