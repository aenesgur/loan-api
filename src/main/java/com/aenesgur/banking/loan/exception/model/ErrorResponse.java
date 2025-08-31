package com.aenesgur.banking.loan.exception.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String code; //TOD: may define error code with error messages
}