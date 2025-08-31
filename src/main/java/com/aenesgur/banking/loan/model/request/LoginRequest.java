package com.aenesgur.banking.loan.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(max = 50, message = "Username must be smaller than 50 characters")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
